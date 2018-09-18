git_repository(
    name = "io_bazel_rules_python",
    remote = "https://github.com/bazelbuild/rules_python.git",
    # Git commit SHA.
    commit = "8b5d0683a7d878b28fffe464779c8a53659fc645",
)

load("@io_bazel_rules_python//python:pip.bzl", "pip_import")

pip_import(
   name = "pso_deps",
   requirements = "//data_analytics/dataflow_python_examples:requirements.txt",
)

load("@pso_deps//:requirements.bzl", _pub_sub_deps_install = "pip_install")
_pub_sub_deps_install()