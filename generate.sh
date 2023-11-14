#!/bin/bash

export LC_ALL=C
export LANG=C

SUCCESSFUL="\xE2\x9C\x85"  # Checkmark emoji
FAILED="\xE2\x9D\x8C"      # Cross Mark emoji
TADA="\xF0\x9F\x8E\x89"    # Tada emoji

ALL_TASKS_SUCCESSFUL=true

# Default values
default_app_name="spring-boot-template"
default_app_port="4326"
default_package="com.lomasz.spring.boot.template"

new_name=""
new_port=""
new_package=""

# Function to display usage
usage() {
    echo "Usage: $0 [--name app_name] [--port new_port] [--package new_package]"
    exit 1
}

# Function to update folder structure
update_folder_structure() {
    local base_path=$1
    local old_package_path="$base_path/$(echo $default_package | tr '.' '/')"
    local new_package_path="$base_path/$(echo $new_package | tr '.' '/')"

    # Create new package directory structure if it does not exist
    mkdir -p "$new_package_path"
    # Copy all files to the new package directory
    cp -R "$old_package_path"/. "$new_package_path"/
    local status=$?
    if [ $status -ne 0 ]; then
        return 1
    fi

    # Remove old package directory structure
    rm -rf "$old_package_path"
    status=$?
    if [ $status -ne 0 ]; then
        return 1
    fi

    # Now, clean up any empty parent directories up to the base path
    local parent_dir=$(dirname "$old_package_path")
    while [[ "$parent_dir" != "$base_path" && "$parent_dir" != "." ]]; do
        rmdir "$parent_dir" 2>/dev/null
        parent_dir=$(dirname "$parent_dir")
    done
}

# Function to find and replace
find_and_replace() {
    find . -type f ! -path "$3" -exec sed -i '' "s/$1/$2/g" {} +
}

# Function to log step and check its success
log_step() {
    local step_name=$1
    shift
    echo -n "$step_name... "
    "$@"  # Execute the command
    local status=$?
    if [ $status -eq 0 ]; then
        echo -e "$SUCCESSFUL"
    else
        echo -e "$FAILED"
        ALL_TASKS_SUCCESSFUL=false
    fi
    return $status
}

# Process arguments
while [ "$1" != "" ]; do
    case $1 in
        --name )    shift
                    new_name=$1
                    ;;
        --port )    shift
                    new_port=$1
                    ;;
        --package ) shift
                    new_package=$1
                    ;;
        * )         usage
                    exit 1
    esac
    shift
done

# Ask for missing information
if [ -z "$new_name" ]; then
    read -p "Enter new application name (default: $default_app_name): " new_name
    new_name=${new_name:-$default_app_name}
fi
if [ -z "$new_port" ]; then
    read -p "Enter new application port (default: $default_app_port): " new_port
    new_port=${new_port:-$default_app_port}
fi
if [ -z "$new_package" ]; then
    read -p "Enter new package name (default: $default_package): " new_package
    new_package=${new_package:-$default_package}
fi

echo "--------------------------------------------------------------------------------"

# Update application name
log_step "Updating application name" find_and_replace "$default_app_name" "$new_name" '*/\.*'

# Update application port
log_step "Updating application port" find_and_replace "$default_app_port" "$new_port" '*/\.*'

# Update package
log_step "Updating package" find_and_replace "$default_package" "$new_package" '*/\.*'

# Update package name in build.gradle
log_step "Updating package name in build.gradle" sed -i '' "s/group = '$default_package'/group = '$new_package'/g" build.gradle

old_package_path="src/main/java/$(echo $default_package | tr '.' '/')"
new_package_path="src/main/java/$(echo $new_package | tr '.' '/')"

log_step "Updating folder structure for main" update_folder_structure "src/main/java"
log_step "Updating folder structure for test" update_folder_structure "src/test/java"
log_step "Updating folder structure for integration tests" update_folder_structure "src/integration/java"

if [ "$ALL_TASKS_SUCCESSFUL" = true ]; then
    echo -e "\033[32mAll tasks completed! $TADA\033[0m"
else
    echo -e "\033[31mSome tasks failed. Please check the logs above. $FAILED\033[0m"
fi
