#!/bin/sh

####################################################################################################################################################
####################################################################################################################################################
####                                                                                                                                            ####
####                This script runs the maven clean install and then moves the jar file and the folder with                                    ####
####                the dependencies to the location where the installed plugins normally reside in the userdir                                 ####
####                of the designer. This means no manual uninstall and install via the plugins window of the designer                          ####
####                                                                                                                                            ####
####################################################################################################################################################
####################################################################################################################################################

# name of the jar file to be moved. Example of the git plugin:
# jarFile="de-adito-git-adito-nbm-git.jar"
jarFile="de-adito-aditoweb-nbm-eslint.jar"
# path to the jar file above, from the location of this script. Example of the git plugin (Note that the git plugin builds the nbm in a submodule,
# path probably starts with target/...):
# jarPath="nbm/target/nbm/netbeans/extra/modules/"
jarPath="target/nbm/netbeans/extra/modules/"
# the path to the folder in which the jar should be moved. Example of the git plugin:
# jarTargetPath="../0.0/workingdir/nbp_userdir/modules/"
jarTargetPath="../20210/workingdir/nbp_userdir/modules/"
# name of the folder that contains the gathered dependencies of the plugin. These have to be moved as well. Example of the git plugin:
# folderName="de.adito.git.adito-nbm-git/"
folderName="de.adito.aditoweb.nbm.eslint"
# path to the folder containing the gathered dependencies, as seen from the location of the script. Example of the git plugin:
# folderPath="nbm/target/nbm/netbeans/extra/modules/ext/"
folderPath="target/nbm/netbeans/extra/modules/ext/"

if test -z "$jarFile" || test -z "$jarPath" || test -z "$jarTargetPath" || test -z "$folderName" || test -z "$folderPath"
then
  echo "Variables for the file paths not set up, aborting the job. Please fill in the variables in the script"

else
targetPathFolder="ext/"
jarFilePath=$jarPath$jarFile
jarFileTargetPath=$jarTargetPath$jarFile
folderNamePath=$folderPath$folderName
folderTargetPath=$jarTargetPath$targetPathFolder

JAVA_HOME="C:/Users/s.seemann/.jdks/adopt-openjdk-13.0.2" "C:\Users\s.seemann\AppData\Local\JetBrains\IntelliJ IDEA Community Edition 2020.2.3\plugins\maven\lib\maven3\bin\mvn" clean install -T 1C -P adito.m2

cp $jarFilePath $jarFileTargetPath
cp -r $folderNamePath $folderTargetPath

fi
