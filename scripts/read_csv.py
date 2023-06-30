import glob
import logging
import os
import shutil
from pathlib import Path

import pandas as pd
import pickle
import numpy as np

# Switch mode
# mode = 'server'
mode = 'local'
if mode == "server":
    search_path = '/home/bowen/Research_Projects/Merge_Conflicts/Resource/MergeMiner_output'
    cvs_path = '/home/bowen/Research_Projects/Merge_Conflicts/Script/github_star_projects/textual_script/Textual_Conflicts_Total.csv'
    script_path = '/home/bowen/Research_Projects/Merge_Conflicts/Script/github_star_projects/textual_script'
    output_path = '/home/bowen/Research_Projects/Merge_Conflicts/Resource/workspace'
elif mode == "local":
    search_path = '/home/ppp/Research_Projects/Merge_Conflicts/Resource/MergeMiner_output/'
    # search_path = '/home/ppp/Research_Projects/Merge_Conflicts/Resource/Textual_Tool_output'
    cvs_path = '/home/ppp/Research_Projects/Merge_Conflicts/Script/github_star_projects/textual_conflict_script/Textual_Conflicts_Total.csv'
    script_path = '/home/ppp/Research_Projects/Merge_Conflicts/Script/github_star_projects/textual_conflict_script'
    output_path = '/home/ppp/Research_Projects/Merge_Conflicts/Resource/Textual_commits'
else:
    # raise AbnormalBehaviourError("Wrong mode")
    pass

# Read csv file into python
df_excel = pd.read_csv(cvs_path)

# # Read pickle
# if os.path.exists(os.path.join(script_path, "missing_list.txt")) and \
#         os.path.isfile(os.path.join(script_path, "missing_list.txt")):
#     with open(os.path.join(script_path, "missing_list.txt"), "rb") as fp:
#         missing_list = pickle.load(fp)

# create logger with 'csv_logger'
logger = logging.getLogger('csv_logger')
logger.setLevel(logging.INFO)
# create file handler which logs even debug messages
fh = logging.FileHandler(os.path.join(script_path, 'textual_conflict.log'))
fh.setLevel(logging.INFO)
# create formatter and add it to the handlers
formatter = logging.Formatter('%(asctime)s - %(levelname)s - %(message)s')
fh.setFormatter(formatter)

# add the handlers to the logger
logger.addHandler(fh)
# Count number of conflicts
rows_count = len(df_excel.index)
# Read column project
list1 = df_excel["Project"]
# Read column commit
list2 = df_excel["Commit"]
# Read column file_name
list3 = df_excel["File Name"]
info_list = list(zip(list1, list2, list3))
# Write pickle
with open(os.path.join(script_path, "total_list.txt"), "wb") as fp:
    pickle.dump(info_list, fp)
# Test code
# rows_count = len(missing_list)
# info_list = missing_list
# missing_list = []
# Create output folder to store all commits if not exist
if not os.path.exists(output_path):
    Path(output_path).mkdir()
elif os.path.isfile(output_path):
    raise Exception("Path: " + output_path + " is file instead of folder")
# Find corresponding folder
os.chdir(os.path.join(search_path))
for i in range(rows_count):
    project = info_list[i][0]
    commit = info_list[i][1]
    matches = glob.glob("**/" + project + "/" + commit, recursive=False)
    if matches:
        if len(matches) == 1:
            print("Find commit " + commit + " in Project " + project + " at path\n" +
                  os.path.join(search_path, matches[0]))
            logger.info("Find commit " + commit + " in Project " + project + " at path\n " +
                        os.path.join(search_path, matches[0]))
            try:
                # if not os.path.exists(os.path.join(output_path, project, commit)):
                #     Path(os.path.join(output_path, project, commit)).mkdir(parents=True)
                # elif os.path.isfile(os.path.join(output_path, project, commit)):
                #     raise Exception("Path: " + os.path.join(output_path, project, commit) +
                #                     " is file instead of folder")
                shutil.copytree(os.path.join(search_path, matches[0]),
                                os.path.join(output_path, project, commit))
                logger.info("Successfully move commit " + commit + " in Project " + project)
            except IOError as e:
                # IO Exception occur
                print(str(e))
        else:
            print("Find multiple commits " + commit + " in Project " + project)
            logger.error("Find multiple commits " + commit + " in Project " + project)
    else:
        print("Cannot find commit " + commit + " in Project " + project)
        logger.error("Cannot find commit " + commit + " in Project " + project)
        # missing_list.append(info_list[i])
# with open(os.path.join(output_path, "missing_list.txt"), "wb") as fp:
#     pickle.dump(missing_list, fp)

# for i in range(rows_count):
#     if pd.isnull(df_excel.iat[i, 0]):
#         # print("Empty")
#         df_excel.iat[i, 0] = df_excel.iat[i-1, 0]
#         df_excel.iat[i, 1] = df_excel.iat[i - 1, 1]
# new_excel = df_excel.to_csv(output_path)
# df_excel.loc[1, 1]