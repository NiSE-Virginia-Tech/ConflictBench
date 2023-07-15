# Script to run experiments
import glob
import os
import logging
import shutil
from pathlib import Path
import pickle
from git import Repo
import subprocess

# Set path
path_prefix = '/home/ppp/Research_Projects/Merge_Conflicts/Test'
workspace = 'Resource/workspace'
logger_path = 'Logger'
data_path = 'Data'
output_path = 'Resource/output'
FSTMerge_executable_path = 'MergeTools/FSTMerge/featurehouse_20220107.jar'
JDime_executable_path = 'MergeTools/JDime/bin/JDime'
IntelliMerge_executable_path = 'MergeTools/IntelliMerge/IntelliMerge-1.0.9-all.jar'
AutoMerge_executable_path = 'MergeTools/AutoMerge/AutoMerge.jar'

# Set constant
# Set the longest waiting time to wait for a task to execute (Unit: minutes)
MAX_WAITINGTIME_MERGE = 5 * 60
MAX_WAITINGTIME_CLONE = 10 * 60
MAX_WAITINGTIME_MERGE_BASE = 1 * 60
MAX_WAITINGTIME_RESET = 5 * 60
MAX_WAITINGTIME_RESTORE = 1*60
MAX_WAITINGTIME_DIFF = 5*60
MAX_WAITINGTIME_LOG = 5*60
MAX_WAITINGTIME_COMPILE = 5*60
MAX_WAITINGTIME_TEST = 10*60
Rename_Threshold = "90%"

# Define Exception
class AbnormalBehaviourError(Exception):
    # Any user-defined abnormal behaviour need to terminate the script can be found here
    def __init__(self, message):
        self.message = message

def merge_with_JDime(input_path, output_path, mode, logger):
    try:
        if mode == 0:
            # linebased+structured
            mode_cmd = "linebased,structured"
        elif mode == 1:
            # structured:
            mode_cmd = "structured"
        else:
            raise AbnormalBehaviourError("Undefined mode in JDime")
        proc = subprocess.Popen(os.path.join(path_prefix, JDime_executable_path) +
                                " " + "-f --mode " + mode_cmd +
                                " --output " + output_path + " " +
                                os.path.join(input_path, "left") + " " +
                                os.path.join(input_path, "base") + " " +
                                os.path.join(input_path, "right"),
                                stdout=subprocess.PIPE, stderr=subprocess.PIPE, shell=True)
        outs, errs = proc.communicate(timeout=MAX_WAITINGTIME_RESTORE)
        if proc.returncode == 0:
            # Update logger
            logger.info("Finish JDime")
        else:
            # Failed to run JDime
            logger.info("Fail to run JDime")
            raise AbnormalBehaviourError("Fail to run JDime")
    except subprocess.TimeoutExpired:
        # Terminate the unfinished process
        proc.terminate()
        # Timeout occur
        # Update logger
        logger.error("Fail to run JDime in time")
        raise AbnormalBehaviourError("Fail to run JDime in time")
    finally:
        pass


def merge_with_FSTMerge(input_path, output_path, logger):
    try:
        # Create merge.config at first
        f = open(os.path.join(input_path, "merge.config"), "w")
        f.write("left\nbase\nright");
        f.close()
        # Run FSTMerge
        proc = subprocess.Popen("java -cp " +
                                os.path.join(path_prefix, FSTMerge_executable_path) +
                                " " + "merger.FSTGenMerger --expression " +
                                os.path.join(input_path, "merge.config") + " > " +
                                os.path.join(output_path, "result.txt"),
                                stdout=subprocess.PIPE, stderr=subprocess.PIPE, shell=True)
        outs, errs = proc.communicate(timeout=MAX_WAITINGTIME_RESTORE)
        if proc.returncode == 0:
            # Update logger
            logger.info("Finish FSTMerge")
            # Move the generated folder into output path
            if os.path.exists(os.path.join(input_path, "merge")) and\
                    not os.path.isfile(os.path.join(input_path, "merge")):
                shutil.move(os.path.join(input_path, "merge"),
                            os.path.join(output_path))
            else:
                raise AbnormalBehaviourError("FSTMerge generated folder doesn't exist")
        else:
            # Failed to run JDime
            logger.info("Fail to run FSTMerge")
            raise AbnormalBehaviourError("Fail to run FSTMerge")
    except subprocess.TimeoutExpired:
        # Terminate the unfinished process
        proc.terminate()
        # Timeout occur
        # Update logger
        logger.error("Fail to run FSTMerge in time")
        raise AbnormalBehaviourError("Fail to run FSTMerge in time")
    finally:
        pass


def merge_with_IntelliMerge(input_path, output_path, logger):
    try:
        # Run IntelliMerge
        proc = subprocess.Popen("java -jar " +
                                os.path.join(path_prefix, IntelliMerge_executable_path) +
                                " " + "-d " +
                                os.path.join(input_path, "left") + " " +
                                os.path.join(input_path, "base") + " " +
                                os.path.join(input_path, "right") + " " +
                                "-o " + output_path,
                                stdout=subprocess.PIPE, stderr=subprocess.PIPE, shell=True)
        outs, errs = proc.communicate(timeout=MAX_WAITINGTIME_RESTORE)
        if proc.returncode == 0:
            # Update logger
            logger.info("Finish IntelliMerge")
        else:
            # Failed to run JDime
            logger.info("Fail to run IntelliMerge")
            raise AbnormalBehaviourError("Fail to run IntelliMerge")
    except subprocess.TimeoutExpired:
        # Terminate the unfinished process
        proc.terminate()
        # Timeout occur
        # Update logger
        logger.error("Fail to run IntelliMerge in time")
        raise AbnormalBehaviourError("Fail to run IntelliMerge in time")
    finally:
        pass


def merge_with_AutoMerge(input_path, output_path, logger):
    try:
        # Run AutoMerge
        proc = subprocess.Popen("java -jar " +
                                os.path.join(path_prefix, AutoMerge_executable_path) +
                                " " + "-o " + output_path + " -m structured -log info -f -S " +
                                os.path.join(input_path, "left") + " " +
                                os.path.join(input_path, "base") + " " +
                                os.path.join(input_path, "right"),
                                stdout=subprocess.PIPE, stderr=subprocess.PIPE, shell=True)
        outs, errs = proc.communicate(timeout=MAX_WAITINGTIME_RESTORE)
        if proc.returncode == 0:
            # Update logger
            logger.info("Finish AutoMerge")
            return True
        else:
            # Failed to run JDime
            logger.info("Fail to run AutoMerge")
            raise AbnormalBehaviourError("Fail to run AutoMerge")
    except subprocess.TimeoutExpired:
        # Terminate the unfinished process
        proc.terminate()
        # Timeout occur
        # Update logger
        logger.error("Fail to run AutoMerge in time")
        raise AbnormalBehaviourError("Fail to run AutoMerge in time")
    finally:
        pass

# merge two commits
def git_merge(right_parent, logger):
    # Use git to merge left and right
    # Assuming repository is currently at left version
    try:
        proc = subprocess.Popen("git merge -s recursive -X find-renames=" + Rename_Threshold + ' ' + right_parent,
                                stdout=subprocess.PIPE, stderr=subprocess.PIPE, shell=True)
        outs, errs = proc.communicate(timeout=MAX_WAITINGTIME_MERGE)
        if proc.returncode == 0:
            # Successful merged by git merge
            return True
        else:
            # Git merge failed
            return False
    except subprocess.TimeoutExpired:
        # Terminate the unfinished process
        proc.terminate()
        # Failed to get result of git merge in time
        logger.error("Fail to run git merge in time")
        raise AbnormalBehaviourError("Fail to run git merge in time")
    finally:
        pass

# clean a folder content
def clean_folder(path, clean_logger):
    if os.path.exists(path):
        # destination exist
        if os.path.isdir(path):
            # destination is a directory
            try:
                for filename in os.listdir(path):
                    file_path = os.path.join(path, filename)
                    if os.path.isfile(file_path) or os.path.islink(file_path):
                        os.remove(file_path)
                    elif os.path.isdir(file_path):
                        shutil.rmtree(file_path)
                        # os.system("rm -rf " + file_path)
            except shutil.Error:
                clean_logger.error("remove folder error occurred")
                raise AbnormalBehaviourError("Certain error occur")
                exit(1)
            # Debugging code: Be cautious
            # except Exception as e:
            #     clean_logger.error("Unknown error")
            #     print(str(e))
            #     raise AbnormalBehaviourError("Certain error occur")
            #     exit(1)
        else:
            raise AbnormalBehaviourError("Path is a file instead of a folder")
    else:
        # Empty path, skip
        pass

def downloadfromgithub(URL,local_path):
    # Clone the repository
    try:
        Repo.clone_from(URL, local_path)
        logger.info("Successfully cloned repository " + URL)
        
    except Exception as e:
        logger.error("Error cloning repository " + URL + ':' + str(e))        
        raise
# Chose to continue experiment
# resume_experiment = True
resume_experiment = False

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
    
    # Read total_list
    if os.path.exists(os.path.join(path_prefix, data_path, "total_list.txt")) and \
            os.path.isfile(os.path.join(path_prefix, data_path, "total_list.txt")):
        with open(os.path.join(path_prefix, data_path, "total_list.txt"), 'r') as f:
            lines = f.readlines()
            total_list = []
            for line in lines:
                parts = line.split('\t')
                # Create a dictionary for each line
                item = {
                    'repo_url': parts[0],
                    'project_name': parts[1],
                    'child_hash': parts[2],
                    'left_hash': parts[3],
                    'right_hash': parts[4],
                    'base_hash': parts[5],
                    'conflicting_file': parts[6].strip(),  # Use strip to remove the newline character at the end of each line
                    }
                total_list.append(item)
    else:
        raise Exception("Cannot find total_list.txt")

    # 
    if(resume_experiment):
        # Pick up where the previous experiment ends
        # Read pickle project_record
        if os.path.exists(os.path.join(path_prefix, data_path, "project_record.txt")) and \
                os.path.isfile(os.path.join(path_prefix, data_path, "project_record.txt")):
            with open(os.path.join(path_prefix, data_path, "project_record.txt"), "rb") as fp:
                project_record = pickle.load(fp)
        else:
            # Cannot find the data file, report error
            raise Exception("Cannot find project_record.txt")
    else:
        # Initiate project_record
        project_record = {}
        project_record['Current_Index'] = 0

    num_list = len(total_list)
    for i in range(project_record['Current_Index'], num_list):
        # Read merge scenario information
        project_url = total_list[i]['repo_url']
        project_name = total_list[i]['project_name']
        commit = {}
        commit['base'] = total_list[i]['base_hash']
        commit['left'] = total_list[i]['left_hash']
        commit['right'] = total_list[i]['right_hash']
        commit['child'] = total_list[i]['child_hash']
        file_path = total_list[i]['conflicting_file']
        project_record['Current_Index'] = i
        logger.info("Start processing index " + str(i) + "\tproject " + project_name + " commit " + commit['child'])

        # Ensure workspace is empty
        clean_folder(os.path.join(path_prefix, workspace), logger)
        # download repository to workspace
        downloadfromgithub(project_url, os.path.join(path_prefix, workspace, project_name))
        # Generate four versions in workspace
        # Make a copy and change to base version
        shutil.copytree(os.path.join(path_prefix, workspace, project_name),
                        os.path.join(path_prefix, workspace, 'base'), symlinks=True)
        os.chdir(os.path.join(path_prefix, workspace, 'base'))
        Repo(os.getcwd()).git.reset('--hard', commit['base'])        
        # git_reset_commit(commit['base'], logger)
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
        Repo(os.getcwd()).git.reset('--hard', commit['left'])
        # git_reset_commit(commit['left'], logger)
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
        Repo(os.getcwd()).git.reset('--hard', commit['right'])
        # git_reset_commit(commit['right'], logger)
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
        Repo(os.getcwd()).git.reset('--hard', commit['child'])
        # git_reset_commit(commit['child'], logger)
        # Except the conflicting file, remove all other files in child version
        os.chdir(os.path.join(path_prefix, workspace, 'child'))
        for filename in glob.glob("**", recursive=True):
            if filename == file_path:
                logger.info("Found same name file in child version")
            if os.path.isfile(filename) and filename != file_path:
                os.remove(filename)
        logger.info("Complete deletion in child version")
        # Run git-merge to get the git-merge version
        # Make a copy and change to left version
        shutil.copytree(os.path.join(path_prefix, workspace, project_name),
                        os.path.join(path_prefix, workspace, 'git-merge'), symlinks=True)
        os.chdir(os.path.join(path_prefix, workspace, 'git-merge'))
        # Reset to left version first
        Repo(os.getcwd()).git.reset('--hard', commit['left'])
        # git_reset_commit(commit['left'], logger)
        # Try to merge with right version
        git_merge(commit['right'], logger)
        # Except the conflicting file, remove all other files in git-merge version
        os.chdir(os.path.join(path_prefix, workspace, 'git-merge'))
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
            # Indicate previous clean up work is not completed
            logger.error("Found existing result folder. Clean up work not finished")
            shutil.rmtree(os.path.join(path_prefix, workspace, 'result'))
            # Create result folder again
            Path(os.path.join(path_prefix, workspace, 'result')).mkdir()
        
        Path(os.path.join(path_prefix, workspace, 'result', 'JDime')).mkdir()
        Path(os.path.join(path_prefix, workspace, 'result', 'AutoMerge')).mkdir()
        Path(os.path.join(path_prefix, workspace, 'result', 'IntelliMerge')).mkdir()
        Path(os.path.join(path_prefix, workspace, 'result', 'FSTMerge')).mkdir()
        # Move the child version into result folder
        shutil.move(os.path.join(path_prefix, workspace, 'child'),                    
                    os.path.join(path_prefix, workspace, 'result'))
        # Move the git-merge version into result folder
        shutil.move(os.path.join(path_prefix, workspace, 'git-merge'),
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
                merge_with_FSTMerge(os.path.join(path_prefix, workspace),
                                                os.path.join(path_prefix, workspace, 'result', 'FSTMerge'), logger)
                commit['FSTMerge_solution_generation'] = True
                logger.info("FSTMerge solution generated")
            except AbnormalBehaviourError as e:
                commit['FSTMerge_solution_generation'] = False
        else:
            # Debug code
            # logger.info("Skip processed index " + str(i) + "\tproject " + project_name + " commit " + commit['child'])
            # # Update project_record.txt in each loop
            # with open(os.path.join(path_prefix, data_path, "project_record.txt"), "wb") as fp:
            #     pickle.dump(project_record, fp)
            # continue
            commit['FSTMerge_mergeable'] = True
            commit['JDime_mergeable'] = True
            commit['IntelliMerge_mergeable'] = True
            commit['AutoMerge_mergeable'] = True
            # Run 1st tool, FSTMerge
            try:
                merge_with_FSTMerge(os.path.join(path_prefix, workspace),
                                                os.path.join(path_prefix, workspace, 'result', 'FSTMerge'), logger)
                commit['FSTMerge_solution_generation'] = True
                logger.info("FSTMerge solution generated")
            except AbnormalBehaviourError as e:
                commit['FSTMerge_solution_generation'] = False
            # Run 2nd tool, JDime
            try:
                merge_with_JDime(os.path.join(path_prefix, workspace),
                                             os.path.join(path_prefix, workspace, 'result', 'JDime'), 1, logger)
                commit['JDime_solution_generation'] = True
                logger.info("JDime solution generated")
            except AbnormalBehaviourError as e:
                commit['JDime_solution_generation'] = False
            # Run 3rd tool, IntelliMerge
            try:
                merge_with_IntelliMerge(os.path.join(path_prefix, workspace),
                                                    os.path.join(path_prefix, workspace, 'result', 'IntelliMerge'), logger)
                commit['IntelliMerge_solution_generation'] = True
                logger.info("IntelliMerge solution generated")
            except AbnormalBehaviourError as e:
                commit['IntelliMerge_solution_generation'] = False
            # Run 4th tool, AutoMerge
            try:
                merge_with_AutoMerge(os.path.join(path_prefix, workspace),
                                                 os.path.join(path_prefix, workspace, 'result', 'AutoMerge'), logger)
                commit['AutoMerge_solution_generation'] = True
                logger.info("AutoMerge solution generated")
            except AbnormalBehaviourError as e:
                commit['AutoMerge_solution_generation'] = False
        logger.info("Complete processing index " + str(i) + "\tproject " + project_name + " commit " + commit['child'])
        
        # Move the base version into result folder
        shutil.move(os.path.join(path_prefix, workspace, 'base'),                    
                    os.path.join(path_prefix, workspace, 'result'))
        # Move the left version into result folder
        shutil.move(os.path.join(path_prefix, workspace, 'left'),                    
                    os.path.join(path_prefix, workspace, 'result'))
        # Move the right version into result folder
        shutil.move(os.path.join(path_prefix, workspace, 'right'),                    
                    os.path.join(path_prefix, workspace, 'result'))
        # Move the entire result folder into output folder
        # Make sure corresponding folder doesn't exist to ensure the behavior of shutil.move()
        if os.path.exists(os.path.join(path_prefix, output_path, project_name, commit['child'])):
            shutil.rmtree(os.path.join(path_prefix, output_path, project_name, commit['child']))
        shutil.move(os.path.join(path_prefix, workspace, 'result'),
                    os.path.join(path_prefix, output_path, project_name, commit['child']))
        # Add commit info into project_record
        project_record[commit['child']] = commit
        # Update project_record.txt in each loop
        with open(os.path.join(path_prefix, data_path, "project_record.txt"), "wb") as fp:
            pickle.dump(project_record, fp)
    # Finish all merge scenarios
    # Ensure workspace is empty
    clean_folder(os.path.join(path_prefix, workspace), logger)
except IOError as e:
    # IO Exception occur
    print(str(e))
    with open(os.path.join(path_prefix, data_path, "project_record.txt"), "wb") as fp:
        pickle.dump(project_record, fp)
except Exception as e:
    # All other exceptions
    print(str(e))
    with open(os.path.join(path_prefix, data_path, "project_record.txt"), "wb") as fp:
        pickle.dump(project_record, fp)
