#!/bin/bash

export LC_ALL=C
export LANG=C

SUCCESSFUL=$'\xE2\x9C\x94'    # Checkmark emoji
FAILED=$'\xe2\x9c\x98'        # Cross Mark emoji
TADA=$'\xF0\x9F\x8E\x89'      # Tada emoji

ALL_TASKS_SUCCESSFUL=true

# Current values
current_app_name="spring-boot-template"
current_app_port="4326"
current_package="com.github.lomasz.spring.boot.template"

new_name=""
new_port=""
new_package=""

# Function to display usage
usage() {
    if [ -t 1 ]; then
        printf "Usage: %s [OPTIONS]\n" "$(basename "$0")"
        printf "\nScript for updating application name, port and package in a Spring Boot project.\n"
        printf "\nOptions:\n"
        printf "  --name app_name\tnew app name, this option cannot be used together with --auto-name\n"
        printf "  --port new_port\tnew port number\n"
        printf "  --package new_package\tnew package name\n"
        printf "  --auto-name\t\tset app name to the current directory name, this option cannot be used together with --name\n"
        printf "\nExamples:\n"
        printf "  %s --name bug-hunter --port 8080 --package com.null.pointer\n" "$(basename "$0")"
        printf "  %s --auto-name\t\t# Automatically uses the name of the current directory\n\n" "$(basename "$0")"
    fi
    exit 1
}

color_print() {
    local message=$1
    local type=$2

    case $type in
        success)
            printf "\e[32m%s\e[0m\n" "$message"
            ;;
        fail)
            printf "\e[31m%s\e[0m\n" "$message"
            ;;
        *)
            printf "%s\n" "$message"  # Default to no color
            ;;
    esac
}

# Function to update folder structure
update_folder_structure() {
    local base_path=$1
    local old_package_path="$base_path/$(echo $current_package | tr '.' '/')"
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
    printf "%s... " "$step_name"
    "$@"  # Execute the command
    local status=$?
    if [ $status -eq 0 ]; then
        color_print "$SUCCESSFUL" success
    else
        color_print "$FAILED" fail
        ALL_TASKS_SUCCESSFUL=false
    fi
    return $status
}

# Flag to check if any argument is provided
any_argument_provided=false

# Process arguments
auto_name=false
name_provided=false

while [ "$1" != "" ]; do
    any_argument_provided=true
    case $1 in
        --name )    shift
                    new_name=$1
                    name_provided=true
                    ;;
        --port )    shift
                    new_port=$1
                    ;;
        --package ) shift
                    new_package=$1
                    ;;
        --auto-name) auto_name=true
                    ;;
        * )         usage
                    exit 1
    esac
    shift
done

# Check if both --name and --auto-name are provided
if [ "$auto_name" = true ] && [ "$name_provided" = true ]; then
    printf "Error: --name and --auto-name cannot be used together.\n"
    exit 1
fi

# Set name to current directory name if --auto-name is used
if [ "$auto_name" = true ]; then
    new_name=$(basename "$(pwd)")
fi

# Ask for missing information only if no arguments were provided
if [ "$any_argument_provided" = false ]; then
    if [ -z "$new_name" ]; then
        read -p "Enter new application name (current: $current_app_name): " new_name
        new_name=${new_name:-$current_app_name}
    fi
    if [ -z "$new_port" ]; then
        read -p "Enter new application port (current: $current_app_port): " new_port
        new_port=${new_port:-$current_app_port}
    fi
    if [ -z "$new_package" ]; then
        read -p "Enter new package name (current: $current_package): " new_package
        new_package=${new_package:-$current_package}
    fi
else
    # Use default values if no value is provided
    new_name=${new_name:-$current_app_name}
    new_port=${new_port:-$current_app_port}
    new_package=${new_package:-$current_package}
fi

printf '%*s\n' "${COLUMNS:-$(tput cols)}" '' | tr ' ' -

# Update application name
log_step "Updating application name" find_and_replace "$current_app_name" "$new_name" '*/\.*'

# Update application port
log_step "Updating application port" find_and_replace "$current_app_port" "$new_port" '*/\.*'

# Update package
log_step "Updating package" find_and_replace "$current_package" "$new_package" '*/\.*'

# Update package name in build.gradle
log_step "Updating package name in build.gradle" sed -i '' "s/group = '$current_package'/group = '$new_package'/g" build.gradle

old_package_path="src/main/java/$(echo $current_package | tr '.' '/')"
new_package_path="src/main/java/$(echo $new_package | tr '.' '/')"

log_step "Updating folder structure for main" update_folder_structure "src/main/java"
log_step "Updating folder structure for test" update_folder_structure "src/test/java"
log_step "Updating folder structure for integration tests" update_folder_structure "src/integration/java"

if [ "$ALL_TASKS_SUCCESSFUL" = true ]; then
    color_print "All tasks completed! $TADA" success
else
    color_print "$FAILED Some tasks failed. Please check the logs above." fail
fi
