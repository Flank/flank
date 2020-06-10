#!/bin/bash

# Helper functions for testing AndroidJUnitRunner in action.
# Can be helpful for checking if flank test filters match AndroidJUnitRunner policy.

PACKAGE="com.example.test_app"
PACKAGE_FOO="com.example.test_app.foo"
PACKAGE_BAR="com.example.test_app.bar"

ANNOTATION="${PACKAGE}.Annotation"

CLASS="${PACKAGE}.InstrumentedTest"
CLASS_FOO="${PACKAGE_FOO}.FooInstrumentedTest"
CLASS_BAR="${PACKAGE_BAR}.BarInstrumentedTest"

METHOD_1="${CLASS}#test1"
METHOD_FOO="${CLASS_FOO}#testFoo"
METHOD_BAR="${CLASS_BAR}#testBar"

RUNNER=com.example.test_app.test/androidx.test.runner.AndroidJUnitRunner

set -euxo pipefail

# should run all tests
function run_instrument {
    adb shell am instrument -r -w $@ ${RUNNER}
}

# should run only method 1, last inclusion filter overrides the rest
function filter_package_bar_class_foo_method_1 {
    run_instrument -e package ${PACKAGE_BAR} -e class ${CLASS_FOO} -e class ${METHOD_1}
}

# should run nothing because of annotation intersect with other filters
function filter_annotation_method_foo {
    run_instrument -e annotation ${ANNOTATION} -e class ${METHOD_FOO}
}

# should exclude both
function filter_notPackage_foo_notClass_bar {
    run_instrument -e notPackage ${PACKAGE_FOO} -e notClass ${CLASS_BAR}
}
