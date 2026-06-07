#!/bin/bash
echo "============================================"
echo " Employee Management System - Build Script"
echo "============================================"

# Create output directory
mkdir -p out

# Compile all Java files (Mac/Linux)
echo "Compiling Java source files..."
javac -cp "lib/sqlite-jdbc-3.47.1.0.jar" -d out -sourcepath src \
    src/Main.java \
    src/model/*.java \
    src/exception/*.java \
    src/database/*.java \
    src/gui/*.java

if [ $? -ne 0 ]; then
    echo ""
    echo "BUILD FAILED. Check error messages above."
    exit 1
fi

echo ""
echo "BUILD SUCCESSFUL!"
echo ""
echo "To run the application:"
echo "  java -cp \"out:lib/sqlite-jdbc-3.47.1.0.jar\" Main"
echo ""
