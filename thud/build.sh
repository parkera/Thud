#!/bin/sh

##########
# Written by YG, 8/6/2002
# Modified by asp, 8/28/2002
##########

echo "Building Thud..."

# Make sure we have our build directory
mkdir -p build

# Compile all files with .java in the name
find . -name "*.java" | xargs javac -d build

# Make the MANIFEST.MF file
sh makeManifest.sh

# Make the .jar file
cp CHANGES.TXT build
cp CHANGES.RTF build
cd build
jar cmf ../MANIFEST.MF Thud.jar CHANGES.TXT CHANGES.RTF btthud/
rm CHANGES.TXT
rm CHANGES.RTF

# Done!
echo "All done. To run Thud, change directory to 'build', then type:"
echo "java -jar Thud"
echo "You may want to remove the files in build/btthud."
echo "Have a nice day."


