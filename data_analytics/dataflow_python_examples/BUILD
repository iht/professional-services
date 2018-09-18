load("@pso_deps//:requirements.bzl", "requirement")

py_binary(
    name = "data_generation_for_benchmarking",
    srcs = ["dataflow_python_examples/data_generation_for_benchmarking.py"],
    deps = [
		requirement("apache_beam"),
        requirement("Faker"),
        requirement("faker_schema"),
        requirement("google_cloud"),
        requirement("google_cloud_bigquery"),
        requirement("google_cloud_dataflow"),
        requirement("google_cloud_pubsub"),
        requirement("google_cloud_storage"),
        requirement("google_cloud_vision"),
        requirement("mock"),
        requirement("numpy"),
        requirement("six"),
    ],
)