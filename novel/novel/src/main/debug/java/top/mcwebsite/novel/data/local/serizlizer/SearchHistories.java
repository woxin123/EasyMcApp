// Generated by the protocol buffer compiler.  DO NOT EDIT!
// source: search_history.proto

package top.mcwebsite.novel.data.local.serizlizer;

/**
 * Protobuf type {@code SearchHistories}
 */
public  final class SearchHistories extends
    com.google.protobuf.GeneratedMessageLite<
        SearchHistories, SearchHistories.Builder> implements
    // @@protoc_insertion_point(message_implements:SearchHistories)
    SearchHistoriesOrBuilder {
  private SearchHistories() {
    histories_ = emptyProtobufList();
  }
  public static final int HISTORIES_FIELD_NUMBER = 1;
  private com.google.protobuf.Internal.ProtobufList<top.mcwebsite.novel.data.local.serizlizer.SearchHistory> histories_;
  /**
   * <code>repeated .SearchHistory histories = 1;</code>
   */
  @java.lang.Override
  public java.util.List<top.mcwebsite.novel.data.local.serizlizer.SearchHistory> getHistoriesList() {
    return histories_;
  }
  /**
   * <code>repeated .SearchHistory histories = 1;</code>
   */
  public java.util.List<? extends top.mcwebsite.novel.data.local.serizlizer.SearchHistoryOrBuilder>
      getHistoriesOrBuilderList() {
    return histories_;
  }
  /**
   * <code>repeated .SearchHistory histories = 1;</code>
   */
  @java.lang.Override
  public int getHistoriesCount() {
    return histories_.size();
  }
  /**
   * <code>repeated .SearchHistory histories = 1;</code>
   */
  @java.lang.Override
  public top.mcwebsite.novel.data.local.serizlizer.SearchHistory getHistories(int index) {
    return histories_.get(index);
  }
  /**
   * <code>repeated .SearchHistory histories = 1;</code>
   */
  public top.mcwebsite.novel.data.local.serizlizer.SearchHistoryOrBuilder getHistoriesOrBuilder(
      int index) {
    return histories_.get(index);
  }
  private void ensureHistoriesIsMutable() {
    if (!histories_.isModifiable()) {
      histories_ =
          com.google.protobuf.GeneratedMessageLite.mutableCopy(histories_);
     }
  }

  /**
   * <code>repeated .SearchHistory histories = 1;</code>
   */
  private void setHistories(
      int index, top.mcwebsite.novel.data.local.serizlizer.SearchHistory value) {
    if (value == null) {
      throw new NullPointerException();
    }
    ensureHistoriesIsMutable();
    histories_.set(index, value);
  }
  /**
   * <code>repeated .SearchHistory histories = 1;</code>
   */
  private void setHistories(
      int index, top.mcwebsite.novel.data.local.serizlizer.SearchHistory.Builder builderForValue) {
    ensureHistoriesIsMutable();
    histories_.set(index, builderForValue.build());
  }
  /**
   * <code>repeated .SearchHistory histories = 1;</code>
   */
  private void addHistories(top.mcwebsite.novel.data.local.serizlizer.SearchHistory value) {
    if (value == null) {
      throw new NullPointerException();
    }
    ensureHistoriesIsMutable();
    histories_.add(value);
  }
  /**
   * <code>repeated .SearchHistory histories = 1;</code>
   */
  private void addHistories(
      int index, top.mcwebsite.novel.data.local.serizlizer.SearchHistory value) {
    if (value == null) {
      throw new NullPointerException();
    }
    ensureHistoriesIsMutable();
    histories_.add(index, value);
  }
  /**
   * <code>repeated .SearchHistory histories = 1;</code>
   */
  private void addHistories(
      top.mcwebsite.novel.data.local.serizlizer.SearchHistory.Builder builderForValue) {
    ensureHistoriesIsMutable();
    histories_.add(builderForValue.build());
  }
  /**
   * <code>repeated .SearchHistory histories = 1;</code>
   */
  private void addHistories(
      int index, top.mcwebsite.novel.data.local.serizlizer.SearchHistory.Builder builderForValue) {
    ensureHistoriesIsMutable();
    histories_.add(index, builderForValue.build());
  }
  /**
   * <code>repeated .SearchHistory histories = 1;</code>
   */
  private void addAllHistories(
      java.lang.Iterable<? extends top.mcwebsite.novel.data.local.serizlizer.SearchHistory> values) {
    ensureHistoriesIsMutable();
    com.google.protobuf.AbstractMessageLite.addAll(
        values, histories_);
  }
  /**
   * <code>repeated .SearchHistory histories = 1;</code>
   */
  private void clearHistories() {
    histories_ = emptyProtobufList();
  }
  /**
   * <code>repeated .SearchHistory histories = 1;</code>
   */
  private void removeHistories(int index) {
    ensureHistoriesIsMutable();
    histories_.remove(index);
  }

  public static top.mcwebsite.novel.data.local.serizlizer.SearchHistories parseFrom(
      java.nio.ByteBuffer data)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return com.google.protobuf.GeneratedMessageLite.parseFrom(
        DEFAULT_INSTANCE, data);
  }
  public static top.mcwebsite.novel.data.local.serizlizer.SearchHistories parseFrom(
      java.nio.ByteBuffer data,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return com.google.protobuf.GeneratedMessageLite.parseFrom(
        DEFAULT_INSTANCE, data, extensionRegistry);
  }
  public static top.mcwebsite.novel.data.local.serizlizer.SearchHistories parseFrom(
      com.google.protobuf.ByteString data)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return com.google.protobuf.GeneratedMessageLite.parseFrom(
        DEFAULT_INSTANCE, data);
  }
  public static top.mcwebsite.novel.data.local.serizlizer.SearchHistories parseFrom(
      com.google.protobuf.ByteString data,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return com.google.protobuf.GeneratedMessageLite.parseFrom(
        DEFAULT_INSTANCE, data, extensionRegistry);
  }
  public static top.mcwebsite.novel.data.local.serizlizer.SearchHistories parseFrom(byte[] data)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return com.google.protobuf.GeneratedMessageLite.parseFrom(
        DEFAULT_INSTANCE, data);
  }
  public static top.mcwebsite.novel.data.local.serizlizer.SearchHistories parseFrom(
      byte[] data,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return com.google.protobuf.GeneratedMessageLite.parseFrom(
        DEFAULT_INSTANCE, data, extensionRegistry);
  }
  public static top.mcwebsite.novel.data.local.serizlizer.SearchHistories parseFrom(java.io.InputStream input)
      throws java.io.IOException {
    return com.google.protobuf.GeneratedMessageLite.parseFrom(
        DEFAULT_INSTANCE, input);
  }
  public static top.mcwebsite.novel.data.local.serizlizer.SearchHistories parseFrom(
      java.io.InputStream input,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws java.io.IOException {
    return com.google.protobuf.GeneratedMessageLite.parseFrom(
        DEFAULT_INSTANCE, input, extensionRegistry);
  }
  public static top.mcwebsite.novel.data.local.serizlizer.SearchHistories parseDelimitedFrom(java.io.InputStream input)
      throws java.io.IOException {
    return parseDelimitedFrom(DEFAULT_INSTANCE, input);
  }
  public static top.mcwebsite.novel.data.local.serizlizer.SearchHistories parseDelimitedFrom(
      java.io.InputStream input,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws java.io.IOException {
    return parseDelimitedFrom(DEFAULT_INSTANCE, input, extensionRegistry);
  }
  public static top.mcwebsite.novel.data.local.serizlizer.SearchHistories parseFrom(
      com.google.protobuf.CodedInputStream input)
      throws java.io.IOException {
    return com.google.protobuf.GeneratedMessageLite.parseFrom(
        DEFAULT_INSTANCE, input);
  }
  public static top.mcwebsite.novel.data.local.serizlizer.SearchHistories parseFrom(
      com.google.protobuf.CodedInputStream input,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws java.io.IOException {
    return com.google.protobuf.GeneratedMessageLite.parseFrom(
        DEFAULT_INSTANCE, input, extensionRegistry);
  }

  public static Builder newBuilder() {
    return (Builder) DEFAULT_INSTANCE.createBuilder();
  }
  public static Builder newBuilder(top.mcwebsite.novel.data.local.serizlizer.SearchHistories prototype) {
    return (Builder) DEFAULT_INSTANCE.createBuilder(prototype);
  }

  /**
   * Protobuf type {@code SearchHistories}
   */
  public static final class Builder extends
      com.google.protobuf.GeneratedMessageLite.Builder<
        top.mcwebsite.novel.data.local.serizlizer.SearchHistories, Builder> implements
      // @@protoc_insertion_point(builder_implements:SearchHistories)
      top.mcwebsite.novel.data.local.serizlizer.SearchHistoriesOrBuilder {
    // Construct using top.mcwebsite.novel.data.local.datastore.SearchHistories.newBuilder()
    private Builder() {
      super(DEFAULT_INSTANCE);
    }


    /**
     * <code>repeated .SearchHistory histories = 1;</code>
     */
    @java.lang.Override
    public java.util.List<top.mcwebsite.novel.data.local.serizlizer.SearchHistory> getHistoriesList() {
      return java.util.Collections.unmodifiableList(
          instance.getHistoriesList());
    }
    /**
     * <code>repeated .SearchHistory histories = 1;</code>
     */
    @java.lang.Override
    public int getHistoriesCount() {
      return instance.getHistoriesCount();
    }/**
     * <code>repeated .SearchHistory histories = 1;</code>
     */
    @java.lang.Override
    public top.mcwebsite.novel.data.local.serizlizer.SearchHistory getHistories(int index) {
      return instance.getHistories(index);
    }
    /**
     * <code>repeated .SearchHistory histories = 1;</code>
     */
    public Builder setHistories(
        int index, top.mcwebsite.novel.data.local.serizlizer.SearchHistory value) {
      copyOnWrite();
      instance.setHistories(index, value);
      return this;
    }
    /**
     * <code>repeated .SearchHistory histories = 1;</code>
     */
    public Builder setHistories(
        int index, top.mcwebsite.novel.data.local.serizlizer.SearchHistory.Builder builderForValue) {
      copyOnWrite();
      instance.setHistories(index, builderForValue);
      return this;
    }
    /**
     * <code>repeated .SearchHistory histories = 1;</code>
     */
    public Builder addHistories(top.mcwebsite.novel.data.local.serizlizer.SearchHistory value) {
      copyOnWrite();
      instance.addHistories(value);
      return this;
    }
    /**
     * <code>repeated .SearchHistory histories = 1;</code>
     */
    public Builder addHistories(
        int index, top.mcwebsite.novel.data.local.serizlizer.SearchHistory value) {
      copyOnWrite();
      instance.addHistories(index, value);
      return this;
    }
    /**
     * <code>repeated .SearchHistory histories = 1;</code>
     */
    public Builder addHistories(
        top.mcwebsite.novel.data.local.serizlizer.SearchHistory.Builder builderForValue) {
      copyOnWrite();
      instance.addHistories(builderForValue);
      return this;
    }
    /**
     * <code>repeated .SearchHistory histories = 1;</code>
     */
    public Builder addHistories(
        int index, top.mcwebsite.novel.data.local.serizlizer.SearchHistory.Builder builderForValue) {
      copyOnWrite();
      instance.addHistories(index, builderForValue);
      return this;
    }
    /**
     * <code>repeated .SearchHistory histories = 1;</code>
     */
    public Builder addAllHistories(
        java.lang.Iterable<? extends top.mcwebsite.novel.data.local.serizlizer.SearchHistory> values) {
      copyOnWrite();
      instance.addAllHistories(values);
      return this;
    }
    /**
     * <code>repeated .SearchHistory histories = 1;</code>
     */
    public Builder clearHistories() {
      copyOnWrite();
      instance.clearHistories();
      return this;
    }
    /**
     * <code>repeated .SearchHistory histories = 1;</code>
     */
    public Builder removeHistories(int index) {
      copyOnWrite();
      instance.removeHistories(index);
      return this;
    }

    // @@protoc_insertion_point(builder_scope:SearchHistories)
  }
  @java.lang.Override
  @java.lang.SuppressWarnings({"unchecked", "fallthrough"})
  protected final java.lang.Object dynamicMethod(
      com.google.protobuf.GeneratedMessageLite.MethodToInvoke method,
      java.lang.Object arg0, java.lang.Object arg1) {
    switch (method) {
      case NEW_MUTABLE_INSTANCE: {
        return new top.mcwebsite.novel.data.local.serizlizer.SearchHistories();
      }
      case NEW_BUILDER: {
        return new Builder();
      }
      case BUILD_MESSAGE_INFO: {
          java.lang.Object[] objects = new java.lang.Object[] {
            "histories_",
            top.mcwebsite.novel.data.local.serizlizer.SearchHistory.class,
          };
          java.lang.String info =
              "\u0000\u0001\u0000\u0000\u0001\u0001\u0001\u0000\u0001\u0000\u0001\u001b";
          return newMessageInfo(DEFAULT_INSTANCE, info, objects);
      }
      // fall through
      case GET_DEFAULT_INSTANCE: {
        return DEFAULT_INSTANCE;
      }
      case GET_PARSER: {
        com.google.protobuf.Parser<top.mcwebsite.novel.data.local.serizlizer.SearchHistories> parser = PARSER;
        if (parser == null) {
          synchronized (top.mcwebsite.novel.data.local.serizlizer.SearchHistories.class) {
            parser = PARSER;
            if (parser == null) {
              parser =
                  new DefaultInstanceBasedParser<top.mcwebsite.novel.data.local.serizlizer.SearchHistories>(
                      DEFAULT_INSTANCE);
              PARSER = parser;
            }
          }
        }
        return parser;
    }
    case GET_MEMOIZED_IS_INITIALIZED: {
      return (byte) 1;
    }
    case SET_MEMOIZED_IS_INITIALIZED: {
      return null;
    }
    }
    throw new UnsupportedOperationException();
  }


  // @@protoc_insertion_point(class_scope:SearchHistories)
  private static final top.mcwebsite.novel.data.local.serizlizer.SearchHistories DEFAULT_INSTANCE;
  static {
    SearchHistories defaultInstance = new SearchHistories();
    // New instances are implicitly immutable so no need to make
    // immutable.
    DEFAULT_INSTANCE = defaultInstance;
    com.google.protobuf.GeneratedMessageLite.registerDefaultInstance(
      SearchHistories.class, defaultInstance);
  }

  public static top.mcwebsite.novel.data.local.serizlizer.SearchHistories getDefaultInstance() {
    return DEFAULT_INSTANCE;
  }

  private static volatile com.google.protobuf.Parser<SearchHistories> PARSER;

  public static com.google.protobuf.Parser<SearchHistories> parser() {
    return DEFAULT_INSTANCE.getParserForType();
  }
}

