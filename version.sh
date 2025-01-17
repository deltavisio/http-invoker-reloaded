#!/bin/bash

print_help() {
    echo "Usage: ./version.sh <command>"
    echo ""
    echo "Commands:"
    echo "  get       Prints the current version of the Maven project."
    echo "  create    Creates a new version tag based on the current date and pushes it to the Git repository."
    echo "  help      Prints this help message."
    echo ""
    echo "If no command is provided, the 'get' command is executed by default."
}

case "$1" in
    get|'')
        export VERSIONING_DISABLE=false
        MAVEN_VERSION=$(mvn help:evaluate -Dexpression=project.version -DforceStdout -q)
        echo "$MAVEN_VERSION"
        ;;
    create)
        # Check for uncommitted changes
        git diff-index --quiet HEAD --
        if [ $? -eq 1 ]; then
            echo "There are uncommitted changes. Please commit or stash them before creating a new version."
            exit 1
        fi

        TAG_VERSION=$(date +%y.%V)
        git tag -m "Start Version $TAG_VERSION" -a s${TAG_VERSION}
        git push --tags
        ;;
    help)
        print_help
        ;;
    *)
        echo "Unknown command: $1"
        print_help
        exit 1
        ;;
esac

