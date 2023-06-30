# MergeMiner Script
# Should extract all commits cannot be automatically merged by git merge
import os
import logging
import subprocess
import shutil
import functools
import collections
from enum import Enum
from pathlib import Path
# import another user module
# from ValueEnum import ValueEnum
# import global variable: mode
# from script import mode

# Data Flow
# 1. Extract all commits contain two parents
# 2. Try to use git merge to merge the commit based on left and right
# 3. If merge operation fail, save the 4 versions


# Pre-defined variable
# Set the logger to record all information
logger = None
# Project information
project_name = None
project_URL = None
# Path
# mode = 'server'
mode = 'local'
if mode == "server":
    Output_path = "/home/bowen/Research_Projects/Merge_Conflicts/Resource/MergeMiner_output"
elif mode == "local":
    Output_path = "/home/ppp/Research_Projects/Merge_Conflicts/Resource/MergeMiner_output"
else:
    # raise AbnormalBehaviourError("Wrong mode")
    pass

Logger_path = Output_path + "/Logger"
# Constant
Rename_Threshold = "90%"
# JDime_executable_path = "/home/ppp/IdeaProjects/JDime/build/install/JDime/bin/JDime"
# Sample command
# git diff --find-renames=90%
# git merge -s recursive -X find-renames=90%"
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


# Pre-defined enum
class ModeEnum(Enum):
    Ant = 1
    Maven = 2
    Gradlew = 3
    Customize = 4


class ResultEnum(Enum):
    False_detection = 1
    True_detection = 2
    NA_detection = 3


# Define Exception
class AbnormalBehaviourError(Exception):
    # Any user-defined abnormal behaviour need to terminate the script can be found here
    def __init__(self, message):
        self.message = message


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


# remove a folder
def remove_folder(path, remove_logger):
    if os.path.exists(path):
        # destination exist
        if os.path.isdir(path):
            # destination is a directory
            try:
                shutil.rmtree(path)
                # os.system("rm -rf " + path)
            except shutil.Error:
                remove_logger.error("remove folder error occurred")
                raise AbnormalBehaviourError("Certain error occur")
                exit(1)
            except Exception:
                remove_logger.error("Unknown error")
                raise AbnormalBehaviourError("Certain error occur")
                exit(1)
        else:
            raise AbnormalBehaviourError("Path is a file instead of a folder")
    else:
        # Empty path, skip
        pass


# Extract the demanding part of path
def extract_file_name(source_path, num_split):
    result = source_path.split('/', num_split)
    path = result[num_split]
    return path


# Convert file path to one string
def convert_path(path):
    # return os.path.abspath(path).replace('/','_')
    return path.replace('/', '_')


# # Process commit, find Textual/Syntactic/Semantic conflicts
# def process_commit(commit, project_info, logger):
#     # Git merge left and right
#     # If git mergeable, go to next step: Automatic compilation
#     # If git unmergeable, check JDime applicable
#     # If JDime not applicable, skip
#     # If JDime applicable, JDime merge all files
#     # If JDime mergeable, go to next step: Automatic compilation
#     # If JDime unmergeable, marked as textual conflict
#     # If Automatic compilation not applicable, skip
#     # If Automatic compilation applicable, compile merged version
#     # If merged version compilation succeed, go to next step: Automatic test
#     # If merged version compilation failed, marked as syntactic conflict
#     # If Automatic test not applicable, skip
#     # If merged version test succeed, marked as clean commit
#     # If merged version test failed, marked as semantic conflict
#     try:
#         # Extract parameters
#         project_name = project_info["Project_name"]
#         project_type = project_info["Type"]
#         base = commit["Base"]
#         left_parent = commit["Left"]
#         right_parent = commit["Right"]
#         child = commit["Child"]
#         # Initialize commit information with unknown status
#         commit["Git_mergeable"] = ValueEnum.unknown
#         commit["Automatic_compilation_applicable"] = ValueEnum.unknown
#         commit["Automatic_compilation_success"] = ValueEnum.unknown
#         commit["Automatic_test_applicable"] = ValueEnum.unknown
#         commit["Automatic_test_success"] = ValueEnum.unknown
#         commit["Textual_conflict_status"] = ValueEnum.unknown
#         commit["Syntactic_conflict_status"] = ValueEnum.unknown
#         commit["Semantic_conflict_status"] = ValueEnum.unknown
#         commit["Clean_commit"] = ValueEnum.unknown
#         commit["Compilable_of_base"] = ValueEnum.unknown
#         commit["Compilable_of_left"] = ValueEnum.unknown
#         commit["Compilable_of_right"] = ValueEnum.unknown
#         commit["Compilable_of_child"] = ValueEnum.unknown
#         commit["Testable_of_base"] = ValueEnum.unknown
#         commit["Testable_of_left"] = ValueEnum.unknown
#         commit["Testable_of_right"] = ValueEnum.unknown
#         commit["Testable_of_child"] = ValueEnum.unknown
#         # create a folder named with commit to store related information
#         commit_folder_path = os.path.join(str(Path(os.getcwd()).parent), child)
#         os.mkdir(commit_folder_path)
#         # write 4 versions into txt
#         with open(os.path.join(commit_folder_path, "4_VERSION_name.txt"), 'w') as f_4:
#             f_4.write("Base\t" + base + '\n')
#             f_4.write("Left\t" + left_parent + '\n')
#             f_4.write("Right\t" + right_parent + '\n')
#             f_4.write("Child\t" + child + '\n')
#
#         # Update logger
#         logger.info("Start process commit\t" + base)
#         # Write diff files no matter conflicts exist or not
#         git_diff(base, left_parent, os.path.join(commit_folder_path, "base_left_diff.txt"), logger)
#         git_diff(base, right_parent, os.path.join(commit_folder_path, "base_right_diff.txt"), logger)
#         git_diff(base, child, os.path.join(commit_folder_path, "base_child_diff.txt"), logger)
#         git_diff(left_parent, child, os.path.join(commit_folder_path, "left_child_diff.txt"), logger)
#         git_diff(right_parent, child, os.path.join(commit_folder_path, "right_child_diff.txt"), logger)
#         # Detect textual conflicts
#         textual_detection_result = detectTextualConflicts(commit, commit_folder_path, logger)
#         if textual_detection_result == ResultEnum.True_detection:
#             # textual conflict detected
#             commit["Textual_conflict_status"] = ValueEnum.true
#             # Move the folder into MergeMiner_output
#             shutil.move(commit_folder_path, os.path.join(Output_path, project_name, child))
#             # Update logger
#             logger.info("Textual Conflict detected. Finish process commit\t" + child)
#             # Reset to child version
#             git_reset_commit(child, logger)
#             return
#         elif textual_detection_result == ResultEnum.False_detection:
#             # no textual conflict detected, go to next step
#             commit["Textual_conflict_status"] = ValueEnum.false
#             # Update logger
#             logger.info("Textual Conflict not detected. Go to next step")
#             # # Reset to child version
#             # git_reset_commit(child, logger)
#             # leave the merged version there
#         elif textual_detection_result == ResultEnum.NA_detection:
#             # Not textual conflicts that we're interested, skip this commit
#             # Update logger
#             logger.info("Other reason for merge failure. Skip this commit\t" + child)
#             # Reset to child version
#             git_reset_commit(child, logger)
#             # remove the commit folder
#             remove_folder(commit_folder_path, logger)
#             return
#         else:
#             # undefined return type
#             raise AbnormalBehaviourError("Undefined return type for textual conflict detection")
#         # Next step should be syntactic and semantic conflicts detection
#         # Make a copy of merged version
#         shutil.copytree(os.getcwd(), os.path.join(commit_folder_path, "merged"), symlinks=True)
#         # Detect syntactic conflicts
#         syntactic_detection_result = detectSyntacticConflicts(commit, project_type, commit_folder_path, logger)
#         if syntactic_detection_result == ResultEnum.True_detection:
#             # Syntactic conflict detected
#             commit["Syntactic_conflict_status"] = ValueEnum.true
#             # Move the folder into MergeMiner_output
#             shutil.move(commit_folder_path, os.path.join(Output_path, project_name, child))
#             # Update logger
#             logger.info("Syntactic Conflict detected. Finish this commit\t" + child)
#             return
#         elif syntactic_detection_result == ResultEnum.False_detection:
#             # No syntactic conflict detected
#             commit["Syntactic_conflict_status"] = ValueEnum.false
#         elif syntactic_detection_result == ResultEnum.NA_detection:
#             # syntactic conflict detection is not applicable because compilable issue of parents
#             # Update logger
#             logger.info("Not applicable for automatic compilation. Skip this commit\t" + child)
#             # remove the commit folder
#             remove_folder(commit_folder_path, logger)
#             return
#         else:
#             # undefined return type
#             raise AbnormalBehaviourError("Undefined return type for syntactic conflict detection")
#         # Detect semantic conflicts
#         semantic_detection_result = detectSemanticConflicts(commit, project_type, commit_folder_path, logger)
#         if semantic_detection_result == ResultEnum.True_detection:
#             # Semantic conflict detected
#             commit["Semantic_conflict_status"] = ValueEnum.true
#             # Move the folder into MergeMiner_output
#             shutil.move(commit_folder_path, os.path.join(Output_path, project_name, child))
#             # Update logger
#             logger.info("Semantic Conflict detected. Finish this commit\t" + child)
#             return
#         elif semantic_detection_result == ResultEnum.False_detection:
#             # No semantic conflict detected
#             commit["Semantic_conflict_status"] = ValueEnum.false
#             # Clean commit, no conflicts at all
#             commit["Clean_commit"] = ValueEnum.true
#             # remove the commit folder
#             remove_folder(commit_folder_path, logger)
#             return
#         elif semantic_detection_result == ResultEnum.NA_detection:
#             # semantic conflict detection is not applicable because testable of parents
#             # Update logger
#             logger.info("Not applicable for automatic test. Skip this commit\t" + child)
#             # remove the commit folder
#             remove_folder(commit_folder_path, logger)
#             return
#         else:
#             raise AbnormalBehaviourError("Undefined return type for semantic conflict detection")
#         # All cases should be returned
#         logger.error("Shouldn't visit here")
#         raise AbnormalBehaviourError("Wrong path to here")
#     # except Exception as e:
#     #     # handle all other exceptions
#     #     print(str(e))
#     finally:
#         pass
#
#
# # Detect Textual Conflicts
# def detectTextualConflicts(commit, commit_folder_path, logger):
#     base = commit["Base"]
#     left_parent = commit["Left"]
#     right_parent = commit["Right"]
#     child = commit["Child"]
#     conflict_files = []
#     terminal_info = []
#     # test commit git mergeable or not
#     if test_mergeable(left_parent, right_parent, terminal_info, logger):
#         # git merge succeed, repository is in merged version
#         # modify commit info
#         commit["Git_mergeable"] = ValueEnum.true
#         # Update logger
#         logger.info("Commit " + child + " succeed to automatically merged by git merge")
#         return ResultEnum.False_detection
#     else:
#         # git merge failed, repository is in merged version with conflict
#         # modify commit info
#         commit["Git_mergeable"] = ValueEnum.false
#         # Update logger
#         logger.info("Commit " + child + " failed to automatically merged by git merge")
#         # Write stdout and stderr information to help manually review
#         with open(os.path.join(commit_folder_path, "textual_conflict_stdout.txt"), 'w') as f_T:
#             f_T.write(terminal_info[0])
#         with open(os.path.join(commit_folder_path, "textual_conflict_stderr.txt"), 'w') as f_T:
#             f_T.write(terminal_info[1])
#         # # Write diff files
#         # git_diff(base, left_parent, os.path.join(commit_folder_path, "base_left_diff.txt"), logger)
#         # git_diff(base, right_parent, os.path.join(commit_folder_path, "base_right_diff.txt"), logger)
#         # git_diff(left_parent, child, os.path.join(commit_folder_path, "left_child_diff.txt"), logger)
#         # git_diff(right_parent, child, os.path.join(commit_folder_path, "right_child_diff.txt"), logger)
#         # # Parsing stdout info to find conflict file
#         # temp = list(filter(lambda x: x.startswith("CONFLICT (content):"), terminal_info[0].split('\n')))
#         # temp = (list(map(lambda x: x.split('CONFLICT (content): Merge conflict in ')[-1], temp)))
#         # for item in temp:
#         #     conflict_files.append(item)
#         # # If conflict content file exists, generate split files
#         # if conflict_files:
#         #     # Write conflict files list
#         #     with open(os.path.join(commit_folder_path, "conflict_files.txt"), 'w') as f_C:
#         #         for file in conflict_files:
#         #             f_C.write("%s\n" % file)
#         #     # Write diff files in separate folders
#         #     for file in conflict_files:
#         #         subfolder_path = os.path.join(commit_folder_path, convert_path(file))
#         #         os.makedirs(subfolder_path)
#         #         git_diff_file(base, left_parent, file,
#         #                       os.path.join(subfolder_path, "base_left_diff.txt"), logger)
#         #         git_diff_file(base, right_parent, file,
#         #                       os.path.join(subfolder_path, "base_right_diff.txt"), logger)
#         #         git_diff_file(left_parent, child, file,
#         #                       os.path.join(subfolder_path, "left_child_diff.txt"), logger)
#         #         git_diff_file(right_parent, child, file,
#         #                       os.path.join(subfolder_path, "right_child_diff.txt"), logger)
#         #         # Get 4 versions of the conflict files
#         #         # Base
#         #         # git restore the conflict file
#         #         git_restore_file(base, file, logger)
#         #         # copy file to destination with the correct extension
#         #         base_file_path = os.path.join(subfolder_path, "_base" + os.path.splitext(file)[-1])
#         #         shutil.copy(os.path.join(os.getcwd(), file), base_file_path)
#         #         # Left
#         #         # git restore the conflict file
#         #         git_restore_file(left_parent, file, logger)
#         #         # copy file to destination with the correct extension
#         #         left_file_path = os.path.join(subfolder_path, "_left" + os.path.splitext(file)[-1])
#         #         shutil.copy(os.path.join(os.getcwd(), file), left_file_path)
#         #         # Right
#         #         # git restore the conflict file
#         #         git_restore_file(right_parent, file, logger)
#         #         # copy file to destination with the correct extension
#         #         right_file_path = os.path.join(subfolder_path, "_right" + os.path.splitext(file)[-1])
#         #         shutil.copy(os.path.join(os.getcwd(), file), right_file_path)
#         #         # Child
#         #         # git restore the conflict file
#         #         git_restore_file(child, file, logger)
#         #         # copy file to destination with the correct extension
#         #         right_file_path = os.path.join(subfolder_path, "_child" + os.path.splitext(file)[-1])
#         #         shutil.copy(os.path.join(os.getcwd(), file), right_file_path)
#         return ResultEnum.True_detection
#
#
# # Detect Syntactic Conflicts
# def detectSyntacticConflicts(commit, project_type, commit_folder_path, logger):
#     base = commit["Base"]
#     left_parent = commit["Left"]
#     right_parent = commit["Right"]
#     child = commit["Child"]
#     # Compile command
#     if project_type == 'Ant':
#         compile_cmd = 'ant clean build'
#     elif project_type == 'Maven':
#         compile_cmd = 'mvn clean compile'
#     elif project_type == 'Gradlew':
#         compile_cmd = './gradlew clean assemble'
#     else:
#         raise AbnormalBehaviourError("Undefined Project Type")
#     # Reset to left version
#     git_reset_commit(left_parent, logger)
#     # Compile left version
#     try:
#         proc = subprocess.Popen(compile_cmd, stdout=subprocess.PIPE, stderr=subprocess.PIPE, shell=True)
#         outs, errs = proc.communicate(timeout=MAX_WAITINGTIME_COMPILE)
#         if proc.returncode == 0:
#             # Succeed to run compilation command in left version
#             commit["Compilable_of_left"] = ValueEnum.true
#             # Update logger
#             logger.info("left version compilation succeed")
#         else:
#             # Failed to run compile command in left version
#             commit["Compilable_of_left"] = ValueEnum.false
#             commit["Automatic_compilation_applicable"] = ValueEnum.false
#             # Update logger
#             logger.info("left version compilation failed")
#             logger.info("Not applicable due to left version compilation failure")
#             return ResultEnum.NA_detection
#     except subprocess.TimeoutExpired:
#         # Terminate the unfinished process
#         proc.terminate()
#         # Timeout occur
#         commit["Compilable_of_left"] = ValueEnum.false
#         commit["Automatic_compilation_applicable"] = ValueEnum.false
#         # Update logger
#         logger.info("left version compilation fail to finish in time")
#         logger.info("Not applicable due to left version compilation timeout")
#         return ResultEnum.NA_detection
#     finally:
#         pass
#     # Reset to right version
#     git_reset_commit(right_parent, logger)
#     # Compile right version
#     try:
#         proc = subprocess.Popen(compile_cmd, stdout=subprocess.PIPE, stderr=subprocess.PIPE, shell=True)
#         outs, errs = proc.communicate(timeout=MAX_WAITINGTIME_COMPILE)
#         if proc.returncode == 0:
#             # Succeed to run compilation command in right version
#             commit["Compilable_of_right"] = ValueEnum.true
#             # Update logger
#             logger.info("right version compilation succeed")
#         else:
#             # Failed to run compile command in right version
#             commit["Compilable_of_right"] = ValueEnum.false
#             commit["Automatic_compilation_applicable"] = ValueEnum.false
#             # Update logger
#             logger.info("right version compilation failed")
#             logger.info("Not applicable due to right version compilation failure")
#             return ResultEnum.NA_detection
#     except subprocess.TimeoutExpired:
#         # Terminate the unfinished process
#         proc.terminate()
#         # Timeout occur
#         commit["Compilable_of_right"] = ValueEnum.false
#         commit["Automatic_compilation_applicable"] = ValueEnum.false
#         # Update logger
#         logger.info("right version compilation fail to finish in time")
#         logger.info("Not applicable due to right version compilation timeout")
#         return ResultEnum.NA_detection
#     finally:
#         pass
#     # Both left and right version compiled successfully, automatic compilation applicable
#     commit["Automatic_compilation_applicable"] = ValueEnum.true
#     # Try to compile merge version
#     original_path = os.getcwd()
#     os.chdir(os.path.join(commit_folder_path, "merged"))
#     # Compile merged version
#     try:
#         proc = subprocess.Popen(compile_cmd, stdout=subprocess.PIPE, stderr=subprocess.PIPE, shell=True)
#         outs, errs = proc.communicate(timeout=MAX_WAITINGTIME_COMPILE)
#         if proc.returncode == 0:
#             # Succeed to run compilation command in merged version
#             commit["Automatic_compilation_success"] = ValueEnum.true
#             # Update logger
#             logger.info("merged version compilation succeed")
#             # Change current path back to original path
#             os.chdir(original_path)
#             return ResultEnum.False_detection
#         else:
#             # Failed to run compile command in merged version
#             commit["Automatic_compilation_success"] = ValueEnum.false
#             # Update logger
#             logger.info("merged version compilation failed")
#             # Write stdout and stderr information to help manually review
#             with open(os.path.join(commit_folder_path, "syntactic_conflict_stdout.txt"), 'w') as f_T:
#                 f_T.write(outs.decode('utf-8'))
#             with open(os.path.join(commit_folder_path, "syntactic_conflict_stderr.txt"), 'w') as f_T:
#                 f_T.write(errs.decode('utf-8'))
#             # To save space, only keep the commit info, remove this merged directory
#             remove_folder(os.path.join(commit_folder_path, 'merged'), logger)
#             # Change current path back to original path
#             os.chdir(original_path)
#             return ResultEnum.True_detection
#     except subprocess.TimeoutExpired:
#         # Terminate the unfinished process
#         proc.terminate()
#         # Timeout occur
#         commit["Automatic_compilation_success"] = ValueEnum.false
#         # Update logger
#         logger.info("Compilation fail to finish in time")
#         # Change current path back to original path
#         os.chdir(original_path)
#         return ResultEnum.True_detection
#     finally:
#         pass
#
#
# # Detect Semantic Conflicts
# def detectSemanticConflicts(commit, project_type, commit_folder_path, logger):
#     base = commit["Base"]
#     left_parent = commit["Left"]
#     right_parent = commit["Right"]
#     child = commit["Child"]
#     # Test command
#     if project_type == 'Ant':
#         test_cmd = 'ant clean test'
#     elif project_type == 'Maven':
#         test_cmd = 'mvn clean test'
#     elif project_type == 'Gradlew':
#         test_cmd = './gradlew clean test'
#     else:
#         raise AbnormalBehaviourError("Undefined Project Type")
#     # Reset to left version
#     git_reset_commit(left_parent, logger)
#     # Test left version
#     try:
#         proc = subprocess.Popen(test_cmd, stdout=subprocess.PIPE, stderr=subprocess.PIPE, shell=True)
#         outs, errs = proc.communicate(timeout=MAX_WAITINGTIME_TEST)
#         if proc.returncode == 0:
#             # Succeed to run test command in left version
#             commit["Testable_of_left"] = ValueEnum.true
#             # Update logger
#             logger.info("left version test succeed")
#         else:
#             # Failed to run test command in left version
#             commit["Testable_of_left"] = ValueEnum.false
#             commit["Automatic_test_applicable"] = ValueEnum.false
#             # Update logger
#             logger.info("left version test failed")
#             logger.info("Not applicable due to left version test failure")
#             return ResultEnum.NA_detection
#     except subprocess.TimeoutExpired:
#         # Terminate the unfinished process
#         proc.terminate()
#         # Timeout occur
#         commit["Testable_of_left"] = ValueEnum.false
#         commit["Automatic_test_applicable"] = ValueEnum.false
#         # Update logger
#         logger.info("left version test fail to finish in time")
#         logger.info("Not applicable due to left version test timeout")
#         return ResultEnum.NA_detection
#     finally:
#         pass
#     # Reset to right version
#     git_reset_commit(right_parent, logger)
#     # Test right version
#     try:
#         proc = subprocess.Popen(test_cmd, stdout=subprocess.PIPE, stderr=subprocess.PIPE, shell=True)
#         outs, errs = proc.communicate(timeout=MAX_WAITINGTIME_TEST)
#         if proc.returncode == 0:
#             # Succeed to run test command in right version
#             commit["Testable_of_right"] = ValueEnum.true
#             # Update logger
#             logger.info("right version test succeed")
#         else:
#             # Failed to run test command in right version
#             commit["Testable_of_right"] = ValueEnum.false
#             commit["Automatic_test_applicable"] = ValueEnum.false
#             # Update logger
#             logger.info("right version test failed")
#             logger.info("Not applicable due to right version test failure")
#             return ResultEnum.NA_detection
#     except subprocess.TimeoutExpired:
#         # Terminate the unfinished process
#         proc.terminate()
#         # Timeout occur
#         commit["Testable_of_right"] = ValueEnum.false
#         commit["Automatic_test_applicable"] = ValueEnum.false
#         # Update logger
#         logger.info("right version test fail to finish in time")
#         logger.info("Not applicable due to right version test timeout")
#         return ResultEnum.NA_detection
#     finally:
#         pass
#     # Both left and right version test successfully, automatic test applicable
#     commit["Automatic_test_applicable"] = ValueEnum.true
#     # Try to test merge version
#     original_path = os.getcwd()
#     os.chdir(os.path.join(commit_folder_path, "merged"))
#     # Test merged version
#     try:
#         proc = subprocess.Popen(test_cmd, stdout=subprocess.PIPE, stderr=subprocess.PIPE, shell=True)
#         outs, errs = proc.communicate(timeout=MAX_WAITINGTIME_TEST)
#         if proc.returncode == 0:
#             # Succeed to run test command in merged version
#             commit["Automatic_test_success"] = ValueEnum.true
#             # Update logger
#             logger.info("merged version test succeed")
#             # Change current path back to original path
#             os.chdir(original_path)
#             return ResultEnum.False_detection
#         else:
#             # Failed to run test command in merged version
#             commit["Automatic_test_success"] = ValueEnum.false
#             # Update logger
#             logger.info("merged version test failed")
#             # Write stdout and stderr information to help manually review
#             with open(os.path.join(commit_folder_path, "semantic_conflict_stdout.txt"), 'w') as f_T:
#                 f_T.write(outs.decode('utf-8'))
#             with open(os.path.join(commit_folder_path, "semantic_conflict_stderr.txt"), 'w') as f_T:
#                 f_T.write(errs.decode('utf-8'))
#             # To save space, only keep the commit info, remove this merged directory
#             remove_folder(os.path.join(commit_folder_path, 'merged'), logger)
#             # Change current path back to original path
#             os.chdir(original_path)
#             return ResultEnum.True_detection
#     except subprocess.TimeoutExpired:
#         # Terminate the unfinished process
#         proc.terminate()
#         # Timeout occur
#         commit["Automatic_test_success"] = ValueEnum.false
#         # Update logger
#         logger.info("test fail to finish in time")
#         # Change current path back to original path
#         os.chdir(original_path)
#         return ResultEnum.True_detection
#     finally:
#         pass


# Based on log file, search all merge commits which have common ancestor
def filter_merge_commits(logger):
    # Initialization
    merge_commits = []
    num_commits = 0
    num_merge_commits = 0
    # Open log file to filter all merge commits with two parents and test it mergable
    with open("../log_file", 'r') as f:
        while True:
            commit_line = f.readline()
            if not commit_line:
                break
            # commit number increase by 1
            num_commits = num_commits + 1
            commit_line = commit_line.split(' ')
            child = commit_line[-1].replace('\n', '')
            # logger.info("Start checking commit\t" + child)
            parent_line = f.readline().split(' ')
            if len(parent_line) == 3:
                # Two parents, test this commit whether can merge automatically
                logger.info("commit\t" + child + " have two parents")
                left_parent = parent_line[1].replace('\n', '')
                right_parent = parent_line[2].replace('\n', '')
                if len(left_parent) != 40:
                    raise AbnormalBehaviourError("Left Parent Hash length is not 40\t" + left_parent)
                elif len(right_parent) != 40:
                    raise AbnormalBehaviourError("Right Parent Hash length is not 40\t" + right_parent)
                # Use git merge-base to get the base commit hash
                base = git_merge_base(left_parent, right_parent, logger)
                # If the base is None, means we don't have the base version here
                if not base:
                    logger.warning("Cannot find base version in commit\t" + child)
                    continue
                else:
                    merge_commits.append({"Base": base, "Left": left_parent, "Right": right_parent, "Child": child})
                    # merge commit number increase by 1
                    num_merge_commits = num_merge_commits + 1
    return {"Num_commits": num_commits, "Num_merge_commits": num_merge_commits, "merge_commits": merge_commits}


# def JDime_merge(merged_file_path, base, left, right, file_name, logger):
#     # Generate the whole command at first
#     # Example commands
#     # "JDime -f --mode linebased,structured --output ./*_merged.java ./*_left.java ./*_base.java ./*_right.java"
#     cmd = JDime_executable_path + " -f --mode linebased,structured --output " \
#           + merged_file_path + " " + left + " " + base + " " + right
#     try:
#         if subprocess.run(cmd, shell=True, timeout=MAX_WAITINGTIME_MERGE).returncode == 0:
#             # Succeed to run JDime
#             # Update logger
#             logger.info("Successfully use JDime to merge " + file_name)
#             return True
#         else:
#             # Failed to run JDime
#             logger.error("Fail to use JDime to merge " + file_name)
#             return False
#     except subprocess.TimeoutExpired:
#         # Timeout occur
#         # Update logger
#         logger.error("Fail to run JDime merge in time")
#         return False
#     finally:
#         pass


def git_clone(url, name, logger):
    try:
        proc = subprocess.Popen("git clone " + url, stdout=subprocess.PIPE, stderr=subprocess.PIPE, shell=True)
        outs, errs = proc.communicate(timeout=MAX_WAITINGTIME_CLONE)
        if proc.returncode == 0:
            # Update logger
            logger.info("Finish clone the project " + name + " from github")
        else:
            # Failed to run git clone
            logger.error("Fail to run git clone " + url)
            raise AbnormalBehaviourError("Fail to run git clone")
    except subprocess.TimeoutExpired:
        # Terminate the unfinished process
        proc.terminate()
        # Timeout occur
        # Update logger
        logger.error("Fail to run git clone in time")
        raise AbnormalBehaviourError("Fail to run git clone in time")
    finally:
        pass


def git_log(path, logger):
    try:
        proc = subprocess.Popen("git log --all --pretty='commit %H%nParent %P' > " + path,
                                stdout=subprocess.PIPE, stderr=subprocess.PIPE, shell=True)
        outs, errs = proc.communicate(timeout=MAX_WAITINGTIME_LOG)
        if proc.returncode == 0:
            # Update logger
            logger.info("Finish output log file in\t" + os.getcwd())
        else:
            # Failed to run git log
            logger.error("Fail to run git log in\t" + os.getcwd())
            raise AbnormalBehaviourError("Fail to run git log in\t" + os.getcwd())
    except subprocess.TimeoutExpired:
        # Terminate the unfinished process
        proc.terminate()
        # Timeout occur
        # Update logger
        logger.error("Fail to run git log\t" + os.getcwd() + "in time")
        raise AbnormalBehaviourError("Fail to run git log in\t" + os.getcwd() + "in time")
    finally:
        pass


def git_reset_commit(commit, logger):
    # Try to reset the repository to a specific commit
    try:
        proc = subprocess.Popen("git reset --hard " + commit,
                                stdout=subprocess.PIPE, stderr=subprocess.PIPE, shell=True)
        outs, errs = proc.communicate(timeout=MAX_WAITINGTIME_RESET)
        if proc.returncode != 0:
            # Failed to reset to target commit
            logger.error("Cannot reset the target commit.")
            logger.error("Fail to reset to commit " + commit)
            raise AbnormalBehaviourError("Fail to reset commit")
    except subprocess.TimeoutExpired:
        # Terminate the unfinished process
        proc.terminate()
        # Failed to reset to target commit in time
        logger.error("Cannot reset the target commit in time.")
        logger.error("Fail to reset to commit " + commit + " in time")
        raise AbnormalBehaviourError("Fail to reset commit in time")
    finally:
        pass


def git_merge_base(left_parent, right_parent, logger):
    # Now try to get the base commit based on left and right
    try:
        proc = subprocess.Popen("git merge-base " + left_parent + ' ' + right_parent,
                                stdout=subprocess.PIPE, stderr=subprocess.PIPE, shell=True)
        outs, errs = proc.communicate(timeout=MAX_WAITINGTIME_MERGE_BASE)
        if proc.returncode == 0:
            base = outs.decode('utf-8').replace('\n', '')
            if len(base) != 40:
                logger.error("Base Commit Hash is not 40\t" + base)
                raise AbnormalBehaviourError("Base Commit Hash is not 40\t" + base)
            else:
                return base
        else:
            # Failed to get result of git merge-base
            # Probably because left and right don't have common ancestor
            logger.warn("Cannot find the base commit.")
            logger.warn("Left parent commit is " + left_parent + " Right parent commit is " + right_parent)
            # raise AbnormalBehaviourError("Fail to run git merge-base")
            return None
    except subprocess.TimeoutExpired:
        # Terminate the unfinished process
        proc.terminate()
        # Failed to get result of git merge-base in time
        logger.error("Fail to run git merge-base in time")
        logger.error("Left parent commit is " + left_parent + " Right parent commit is " + right_parent)
        raise AbnormalBehaviourError("Fail to run git merge-base in time")
    finally:
        pass


def git_diff(left, right, path, logger):
    try:
        proc = subprocess.Popen("git diff --find-renames=" + Rename_Threshold + ' ' + left + ' ' + right + " > "
                                + os.path.abspath(path),
                                stdout=subprocess.PIPE, stderr=subprocess.PIPE, shell=True)
        outs, errs = proc.communicate(timeout=MAX_WAITINGTIME_DIFF)
        if proc.returncode == 0:
            # Update logger
            logger.info("Finish write diff between " + left + ' and ' + right)
        else:
            # Failed to run git log
            logger.error("Fail to write diff between " + left + ' and ' + right)
            raise AbnormalBehaviourError("Fail to write diff")
    except subprocess.TimeoutExpired:
        # Terminate the unfinished process
        proc.terminate()
        # Timeout occur
        # Update logger
        logger.error("Fail to write diff between " + left + ' and ' + right + " in time")
        raise AbnormalBehaviourError("Fail to write diff in time")
    finally:
        pass


def git_diff_file(left, right, file, path, logger):
    try:
        proc = subprocess.Popen("git diff --find-renames=" + Rename_Threshold + ' ' + left + ' ' + right + ' '
                                + os.path.abspath(file) + " > " + os.path.abspath(path),
                                stdout=subprocess.PIPE, stderr=subprocess.PIPE, shell=True)
        outs, errs = proc.communicate(timeout=MAX_WAITINGTIME_DIFF)
        if proc.returncode == 0:
            # Update logger
            logger.info("Finish write diff file between " + left + ' and ' + right)
        else:
            # Failed to run git log
            logger.error("Fail to write diff between " + left + ' and ' + right)
            raise AbnormalBehaviourError("Fail to write diff file")
    except subprocess.TimeoutExpired:
        # Terminate the unfinished process
        proc.terminate()
        # Timeout occur
        # Update logger
        logger.error("Fail to write diff between " + left + ' and ' + right + " in time")
        raise AbnormalBehaviourError("Fail to write diff file in time")
    finally:
        pass


def git_restore_file(commit, file, logger):
    try:
        proc = subprocess.Popen("git restore -s " + commit + ' ' + file,
                                stdout=subprocess.PIPE, stderr=subprocess.PIPE, shell=True)
        outs, errs = proc.communicate(timeout=MAX_WAITINGTIME_RESTORE)
        if proc.returncode == 0:
            # Update logger
            logger.info("Finish restore to " + commit + " in file " + file)
        else:
            # Failed to run git restore
            logger.info("Finish restore to " + commit + " in file " + file)
            raise AbnormalBehaviourError("Fail to git restore")
    except subprocess.TimeoutExpired:
        # Terminate the unfinished process
        proc.terminate()
        # Timeout occur
        # Update logger
        logger.error("Finish restore to " + commit + " in file" + file + " in time")
        raise AbnormalBehaviourError("Fail to git restore in time")
    finally:
        pass


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


def test_mergeable(left_parent, right_parent, terminal_info, logger):
    # Now try to test whether this commit mergeable
    try:
        # First reset to left parent
        git_reset_commit(left_parent, logger)
        # Then merge with right parent
        proc = subprocess.Popen("git merge -s recursive -X find-renames=" + Rename_Threshold + ' ' + right_parent,
                                stdout=subprocess.PIPE, stderr=subprocess.PIPE, shell=True)
        outs, errs = proc.communicate(timeout=MAX_WAITINGTIME_MERGE)
        if proc.returncode == 0:
            # Successful merged by git merge, skip this commit
            return True
        else:
            # record stdout info
            terminal_info.append(outs.decode('utf-8'))
            # record stderr info
            terminal_info.append(errs.decode('utf-8'))
            return False
    except subprocess.TimeoutExpired:
        # Terminate the unfinished process
        proc.terminate()
        # Failed to get result of git merge in time
        logger.error("Fail to run git merge in time")
        logger.error("Left parent commit is " + left_parent + " Right parent commit is " + right_parent)
        raise AbnormalBehaviourError("Fail to run git merge in time")
    finally:
        pass

def remove_all_files_exception(file_path, logger):
    # Except the conflicting file, try to remove all other files
    try:
        proc = subprocess.Popen("find . -type f -not -name " + file_path + " -delete",
                                stdout=subprocess.PIPE, stderr=subprocess.PIPE, shell=True)
        outs, errs = proc.communicate(timeout=MAX_WAITINGTIME_RESET)
        if proc.returncode != 0:
            # Failed to get result of git merge-base
            logger.error("Cannot remove files")
            logger.error("Fail to remove files in " + os.getcwd())
            raise AbnormalBehaviourError("Fail to remove files")
    except subprocess.TimeoutExpired:
        # Terminate the unfinished process
        proc.terminate()
        # Failed to get result of git merge-base in time
        logger.error("Cannot remove files in time.")
        logger.error("Fail to remove files in " + os.getcwd() + " in time")
        raise AbnormalBehaviourError("Fail to reset commit in time")
    finally:
        pass


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
        proc = subprocess.Popen("/home/ppp/IdeaProjects/JDime/build/install/JDime/bin/JDime"
                                " -f --mode " + mode_cmd +
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
        proc = subprocess.Popen("java -cp "
                                "/home/ppp/Research_Projects/Merge_Conflicts/Resource/Textual_Tool/featurehouse_20220107.jar "
                                "merger.FSTGenMerger --expression " +
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
        proc = subprocess.Popen("java -jar "
                                "/home/ppp/Research_Projects/Merge_Conflicts/Resource/Textual_Tool/IntelliMerge-1.0.9-all.jar "
                                "-d " +
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
        proc = subprocess.Popen("java -jar "
                                "/home/ppp/Research_Projects/Merge_Conflicts/Resource/Textual_Tool/AutoMerge.jar "
                                "-o " + output_path + " -m structured -log info -f -S " +
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