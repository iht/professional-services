package com.google.cloud.pso.examples;

import com.google.cloud.pso.examples.data.TaxiRidePoint;
import org.apache.beam.runners.dataflow.options.DataflowPipelineOptions;
import org.apache.beam.sdk.Pipeline;
import org.apache.beam.sdk.extensions.jackson.ParseJsons;
import org.apache.beam.sdk.io.TextIO;
import org.apache.beam.sdk.io.gcp.pubsub.PubsubIO;
import org.apache.beam.sdk.options.PipelineOptionsFactory;
import org.apache.beam.sdk.options.Validation;
import org.apache.beam.sdk.transforms.MapElements;
import org.apache.beam.sdk.values.PCollection;

import static org.apache.beam.sdk.values.TypeDescriptors.strings;

public class ExampleStreamingPipeline {

  public static void main(String[] args) {
    /** Main function for the pipeline. */
    MyPipelineOptions opts =
        PipelineOptionsFactory.fromArgs(args).create().as(MyPipelineOptions.class);

    // Input options
    String pubsubTopic = opts.getTopic();

    // Output options
//    String project = opts.getProject();
//    String dataset = opts.getBigQueryDataset();
//    String table = opts.getBigQueryTable();

    Pipeline p = Pipeline.create(opts);

    PCollection<String> msgs = p.apply(PubsubIO.readStrings().fromTopic(pubsubTopic));
    PCollection<TaxiRidePoint> taxiRidePoints = msgs.apply(ParseJsons.of(TaxiRidePoint.class));

    // For testing purposes
    PCollection<String> strs =
        taxiRidePoints.apply(MapElements.into(strings()).via(t -> t.toString()));

    strs.apply(TextIO.write().to("/tmp/msgs.txt"));
  }

  public interface MyPipelineOptions extends DataflowPipelineOptions {
    /**
     * Custom pipeline options interface.
     *
     * <p>We use {@link DataflowPipelineOptions} so we can find out the project id used to launch
     * the pipeline.
     */
    public String getTopic();

    @Validation.Required
    public void setTopic(String s);

//    public String getBigQueryDataset();
//
//    @Validation.Required
//    public void setBigQueryDataset(String s);
//
//    public String getBigQueryTable();
//
//    @Validation.Required
//    public void setBigQueryTable(String s);
  }
}
