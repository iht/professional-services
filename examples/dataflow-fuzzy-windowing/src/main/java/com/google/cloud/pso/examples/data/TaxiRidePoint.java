package com.google.cloud.pso.examples.data;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.google.auto.value.AutoValue;
import org.codehaus.jackson.annotate.JsonProperty;

@AutoValue
@JsonDeserialize(builder = AutoValue_TaxiRidePoint.class)
public abstract class TaxiRidePoint {
  /**
   * This is a data class for the objects that are read by the pipeline.
   *
   * <p>In this case, it represents part of a taxi ride (a "point"), that is read from a public
   * PubSub topic (<tt>projects/pubsub-public-data/topics/taxirides-realtime</tt>).
   *
   * <p>The data is encoded in JSON in PubSub, and here we include some annotations to map the JSON
   * field names to Java-style variable names.
   */
  public static AutoValue.Builder newBuilder() {
    return new AutoValue_TaxiRidePoint.Builder();
  }

  @JsonProperty("ride_id")
  public abstract String rideId();

  /*
  @SerializedName("ride_id")
  @Nullable
  private String rideId;

  @SerializedName("point_idx")
  @Nullable
  private int pointIdx;

  @Nullable private double latitude;
  @Nullable private double longitude;
  @Nullable private String timestamp;

  @SerializedName("meter_reading")
  @Nullable
  private double meterReading;

  @SerializedName("meter_increment")
  @Nullable
  private double meterIncrement;

  @SerializedName("passenger_count")
  @Nullable
  private int paxCount;
       */
}
