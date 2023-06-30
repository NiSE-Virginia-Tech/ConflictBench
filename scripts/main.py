# Script to pre-select projects in github
import glob
import json
import os
import logging
import shutil
import subprocess
from pathlib import Path
import pickle
import pysftp
# import another user module
import Merge_Miner

# global mode to select server/local
global mode
# mode = 'server'
mode = 'local'
resume_project = True


# Set path
if mode == 'server':
    path_prefix = '/home/bowen/Research_Projects/Merge_Conflicts'
elif mode == 'local':
    path_prefix = '/home/ppp/Research_Projects/Merge_Conflicts'
# else:
#     raise MergeMiner.AbnormalBehaviourError('Wrong mode')


repository_path = 'Resource/Original_copy_projects'
info_path = 'Resource/Textual_commits'
workspace = 'Resource/workspace'
logger_path = 'Script/github_star_projects/textual_conflict_script/logger'
script_path = 'Script/github_star_projects/textual_conflict_script'
output_path = 'Resource/Textual_Tool_output'
# report_path = 'Script/github_star_projects/report'
# commit_path = 'Resource/Commits_info'


# To do: download the repositories from github directly


# Global variable (access with module)
project_record = {}
project_record['Current_Index'] = 0
# create logger to record complete info
# create logger with 'script_logger'
logger = logging.getLogger('textual_conflict_logger')
logger.setLevel(logging.INFO)
# create file handler which logs even debug messages
fh = logging.FileHandler(os.path.join(path_prefix, logger_path, 'textual_conflict.log'))
# fh = logging.FileHandler('script.log')
fh.setLevel(logging.INFO)
# create formatter and add it to the handlers
formatter = logging.Formatter('%(asctime)s - %(levelname)s - %(message)s')
fh.setFormatter(formatter)
# add the handlers to the logger
logger.addHandler(fh)


try:
    # # Generate project information
    # project_record = {}
    # Assign repository name and commit hash
    # Read pickle total_list
    if os.path.exists(os.path.join(path_prefix, script_path, "total_list.txt")) and \
            os.path.isfile(os.path.join(path_prefix, script_path, "total_list.txt")):
        with open(os.path.join(path_prefix, script_path, "total_list.txt"), "rb") as fp:
            total_list = pickle.load(fp)
    else:
        raise Exception("Cannot find total_list.txt")

    # Read pickle project_record
    if os.path.exists(os.path.join(path_prefix, script_path, "project_record.txt")) and \
            os.path.isfile(os.path.join(path_prefix, script_path, "project_record.txt")):
        with open(os.path.join(path_prefix, script_path, "project_record.txt"), "rb") as fp:
            project_record = pickle.load(fp)
    else:
        raise Exception("Cannot find project_record.txt")
    num_list = len(total_list)
    # Test code: redo experiment because not java file or empty file
    # project_record['Current_Index'] = 0
    for i in range(project_record['Current_Index'], num_list):
        project_name = total_list[i][0]
        commit_hash = total_list[i][1]
        file_path = total_list[i][2]
        project_record['Current_Index'] = i
        logger.info("Start processing index " + str(i) + "\tproject " + project_name + " commit " + commit_hash)
        print("Start processing index " + str(i) + "\tproject " + project_name + " commit " + commit_hash)
        # Read four versions
        with open(os.path.join(path_prefix, info_path, project_name, commit_hash, "4_VERSION_name.txt"), 'r') as fp:
            four_version = [line.rstrip('\n') for line in fp]
            commit = {}
            if len(four_version) == 4:
                commit['base'] = four_version[0].split('\t')[1]
                commit['left'] = four_version[1].split('\t')[1]
                commit['right'] = four_version[2].split('\t')[1]
                commit['child'] = four_version[3].split('\t')[1]
            else:
                raise Exception
        # Generate four versions in workspace
        # Ensure workspace is empty
        Merge_Miner.clean_folder(os.path.join(path_prefix, workspace), logger)
        # Make a copy of the origin version to workspace
        # Check whether project exist in repository_path
        if os.path.exists(os.path.join(path_prefix, repository_path, project_name)):
            shutil.copytree(os.path.join(path_prefix, repository_path, project_name),
                            os.path.join(path_prefix, workspace, project_name), symlinks=True)
        else:
            # Copy from server, download to workspace
            downloadfromserver(project_name)
        # Make a copy and change to base version
        shutil.copytree(os.path.join(path_prefix, workspace, project_name),
                        os.path.join(path_prefix, workspace, 'base'), symlinks=True)
        os.chdir(os.path.join(path_prefix, workspace, 'base'))
        Merge_Miner.git_reset_commit(commit['base'], logger)
        # Except the conflicting file, remove all other files in base version
        os.chdir(os.path.join(path_prefix, workspace, 'base'))
        for filename in glob.glob("**", recursive=True):
            if filename == file_path:
                logger.info("Found same name file in base version")
            if os.path.isfile(filename) and filename != file_path:
                os.remove(filename)
        logger.info("Complete deletion in base version")
        # Make a copy and change to left version
        shutil.copytree(os.path.join(path_prefix, workspace, project_name),
                        os.path.join(path_prefix, workspace, 'left'), symlinks=True)
        os.chdir(os.path.join(path_prefix, workspace, 'left'))
        Merge_Miner.git_reset_commit(commit['left'], logger)
        # Except the conflicting file, remove all other files in left version
        os.chdir(os.path.join(path_prefix, workspace, 'left'))
        for filename in glob.glob("**", recursive=True):
            if filename == file_path:
                logger.info("Found same name file in left version")
            if os.path.isfile(filename) and filename != file_path:
                os.remove(filename)
        logger.info("Complete deletion in left version")
        # Make a copy and change to right version
        shutil.copytree(os.path.join(path_prefix, workspace, project_name),
                        os.path.join(path_prefix, workspace, 'right'), symlinks=True)
        os.chdir(os.path.join(path_prefix, workspace, 'right'))
        Merge_Miner.git_reset_commit(commit['right'], logger)
        # Except the conflicting file, remove all other files in right version
        os.chdir(os.path.join(path_prefix, workspace, 'right'))
        for filename in glob.glob("**", recursive=True):
            if filename == file_path:
                logger.info("Found same name file in right version")
            if os.path.isfile(filename) and filename != file_path:
                os.remove(filename)
        logger.info("Complete deletion in right version")
        # Make a copy and change to child version
        shutil.copytree(os.path.join(path_prefix, workspace, project_name),
                        os.path.join(path_prefix, workspace, 'child'), symlinks=True)
        os.chdir(os.path.join(path_prefix, workspace, 'child'))
        Merge_Miner.git_reset_commit(commit['child'], logger)
        # Except the conflicting file, remove all other files in child version
        os.chdir(os.path.join(path_prefix, workspace, 'child'))
        for filename in glob.glob("**", recursive=True):
            if filename == file_path:
                logger.info("Found same name file in child version")
            if os.path.isfile(filename) and filename != file_path:
                os.remove(filename)
        logger.info("Complete deletion in child version")
        # Run git-merge to get the merge version
        # Make a copy and change to left version
        shutil.copytree(os.path.join(path_prefix, workspace, project_name),
                        os.path.join(path_prefix, workspace, 'merge'), symlinks=True)
        os.chdir(os.path.join(path_prefix, workspace, 'merge'))
        # Reset to left version first
        Merge_Miner.git_reset_commit(commit['left'], logger)
        # Try to merge with right version
        Merge_Miner.git_merge(commit['right'], logger)
        # Except the conflicting file, remove all other files in git-merge version
        os.chdir(os.path.join(path_prefix, workspace, 'merge'))
        for filename in glob.glob("**", recursive=True):
            if filename == file_path:
                logger.info("Found same name file in git-merge version")
            if os.path.isfile(filename) and filename != file_path:
                os.remove(filename)
        logger.info("Complete deletion in git-merge version")
        # Check whether input is java file
        if file_path.endswith('java'):
            java_file_format = True
            logger.info("conflict file is java file")
        else:
            java_file_format = False
            logger.info("conflict file is not java file")
        # Check whether base/left/right versions have the corresponding file to merge
        empty_file = False
        # Check base version
        if not os.path.exists(os.path.join(path_prefix, workspace, "base", file_path)):
            logger.info("Cannot find corresponding file in base version, skip multiple tools")
            empty_file = True
        elif not os.path.isfile(os.path.join(path_prefix, workspace, "base", file_path)):
            logger.error("Wrong conflicting file path in base version, refer to a folder")
            raise Exception("Wrong conflicting file path in base version, refer to a folder")
        # Check left version
        if not os.path.exists(os.path.join(path_prefix, workspace, "left", file_path)):
            logger.info("Cannot find corresponding file in left version, skip multiple tools")
            empty_file = True
        elif not os.path.isfile(os.path.join(path_prefix, workspace, "left", file_path)):
            logger.error("Wrong conflicting file path in left version, refer to a folder")
            raise Exception("Wrong conflicting file path in left version, refer to a folder")
        # Check right version
        if not os.path.exists(os.path.join(path_prefix, workspace, "right", file_path)):
            logger.info("Cannot find corresponding file in right version, skip multiple tools")
            empty_file = True
        elif not os.path.isfile(os.path.join(path_prefix, workspace, "right", file_path)):
            logger.error("Wrong conflicting file path in right version, refer to a folder")
            raise Exception("Wrong conflicting file path in right version, refer to a folder")
        # Create result folder
        if not os.path.exists(os.path.join(path_prefix, workspace, 'result')):
            Path(os.path.join(path_prefix, workspace, 'result')).mkdir()
        else:
            # Mean previous clean up work is not completed
            logger.error("Found existing result folder. Clean up work not finished")
            shutil.rmtree(os.path.join(path_prefix, workspace, 'result'))
            # Create result folder again
            Path(os.path.join(path_prefix, workspace, 'result')).mkdir()
        # No need to create moved folder
        # Path(os.path.join(path_prefix, workspace, 'result', 'child')).mkdir()
        # Path(os.path.join(path_prefix, workspace, 'result', 'Git')).mkdir()
        Path(os.path.join(path_prefix, workspace, 'result', 'JDime')).mkdir()
        Path(os.path.join(path_prefix, workspace, 'result', 'AutoMerge')).mkdir()
        Path(os.path.join(path_prefix, workspace, 'result', 'IntelliMerge')).mkdir()
        Path(os.path.join(path_prefix, workspace, 'result', 'FSTMerge')).mkdir()
        # Move the git merge result into result folder
        shutil.move(os.path.join(path_prefix, workspace, 'child'),
                    # os.path.join(path_prefix, workspace, 'result', 'child'))
                    os.path.join(path_prefix, workspace, 'result'))
        # Move the child version into result folder
        shutil.move(os.path.join(path_prefix, workspace, 'merge'),
                    os.path.join(path_prefix, workspace, 'result'))
        # If empty_file is True or file is not java file
        # skip JDime, IntelliMerge, AutoMerge, only keep FSTMerge
        if empty_file or not java_file_format:
            commit['FSTMerge_mergeable'] = True
            commit['JDime_mergeable'] = False
            commit['IntelliMerge_mergeable'] = False
            commit['AutoMerge_mergeable'] = False
            commit['FSTMerge_solution_generation'] = True
            commit['JDime_solution_generation'] = False
            commit['IntelliMerge_solution_generation'] = False
            commit['AutoMerge_solution_generation'] = False
            # Only Run FSTMerge tool
            try:
                Merge_Miner.merge_with_FSTMerge(os.path.join(path_prefix, workspace),
                                                os.path.join(path_prefix, workspace, 'result', 'FSTMerge'), logger)
                commit['FSTMerge_solution_generation'] = True
                logger.info("FSTMerge solution generated")
            except Merge_Miner.AbnormalBehaviourError as e:
                commit['FSTMerge_solution_generation'] = False
        else:
            # Debug code
            # logger.info("Skip processed index " + str(i) + "\tproject " + project_name + " commit " + commit_hash)
            # # Update project_record.txt in each loop
            # with open(os.path.join(path_prefix, script_path, "project_record.txt"), "wb") as fp:
            #     pickle.dump(project_record, fp)
            # continue
            commit['FSTMerge_mergeable'] = True
            commit['JDime_mergeable'] = True
            commit['IntelliMerge_mergeable'] = True
            commit['AutoMerge_mergeable'] = True
            # Run 1st tool, FSTMerge
            try:
                Merge_Miner.merge_with_FSTMerge(os.path.join(path_prefix, workspace),
                                                os.path.join(path_prefix, workspace, 'result', 'FSTMerge'), logger)
                commit['FSTMerge_solution_generation'] = True
                logger.info("FSTMerge solution generated")
            except Merge_Miner.AbnormalBehaviourError as e:
                commit['FSTMerge_solution_generation'] = False
            # Run 2nd tool, JDime
            try:
                Merge_Miner.merge_with_JDime(os.path.join(path_prefix, workspace),
                                             os.path.join(path_prefix, workspace, 'result', 'JDime'), 1, logger)
                commit['JDime_solution_generation'] = True
                logger.info("JDime solution generated")
            except Merge_Miner.AbnormalBehaviourError as e:
                commit['JDime_solution_generation'] = False
            # Run 3rd tool, IntelliMerge
            try:
                Merge_Miner.merge_with_IntelliMerge(os.path.join(path_prefix, workspace),
                                                    os.path.join(path_prefix, workspace, 'result', 'IntelliMerge'), logger)
                commit['IntelliMerge_solution_generation'] = True
                logger.info("IntelliMerge solution generated")
            except Merge_Miner.AbnormalBehaviourError as e:
                commit['IntelliMerge_solution_generation'] = False
            # Run 4th tool, AutoMerge
            try:
                Merge_Miner.merge_with_AutoMerge(os.path.join(path_prefix, workspace),
                                                 os.path.join(path_prefix, workspace, 'result', 'AutoMerge'), logger)
                commit['AutoMerge_solution_generation'] = True
                logger.info("AutoMerge solution generated")
            except Merge_Miner.AbnormalBehaviourError as e:
                commit['AutoMerge_solution_generation'] = False
        # logger.info("Complete processing index " + str(i) + "\tproject " + project_name + " commit " + commit_hash)
        logger.info("Redo complete index " + str(i) + "\tproject " + project_name + " commit " + commit_hash)
        # Move the entire result folder into output folder
        shutil.move(os.path.join(path_prefix, workspace, 'result'),
                    os.path.join(path_prefix, output_path, project_name, commit_hash))
        # Add commit info into project_record
        project_record[commit_hash] = commit
        # Update project_record.txt in each loop
        with open(os.path.join(path_prefix, script_path, "project_record.txt"), "wb") as fp:
            pickle.dump(project_record, fp)
except IOError as e:
    # IO Exception occur
    print(str(e))
    with open(os.path.join(path_prefix, script_path, "project_record.txt"), "wb") as fp:
        pickle.dump(project_record, fp)
except Exception as e:
    # All other exceptions
    print(str(e))
    with open(os.path.join(path_prefix, script_path, "project_record.txt"), "wb") as fp:
        pickle.dump(project_record, fp)
