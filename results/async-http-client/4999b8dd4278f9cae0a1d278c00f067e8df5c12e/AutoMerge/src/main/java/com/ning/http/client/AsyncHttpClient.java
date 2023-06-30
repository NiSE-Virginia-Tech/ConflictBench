package com.ning.http.client;
import com.ning.http.client.providers.NettyAsyncHttpProvider;
import java.io.IOException;
import java.util.concurrent.Future;

/**
 * This class support asynchronous and synchronous HTTP request.
 *
 * To execute synchronous HTTP request, you just need to do
 * {@code
 *    AsyncHttpClient c = new AsyncHttpClient();
 *    Future<Response> f = c.doGet("http://www.ning.com/").get();
 * }
 *
 * The code above will block until the response is fully received. To execute asynchronous HTTP request, you
 * create an {@link AsyncHandler}
 *
 * {@code
 *       AsyncHttpClient c = new AsyncHttpClient();
 *       Future<Response> f = c.doGet("http://www.ning.com/", new AsyncHandler<Response>() &#123;
 *
 *          @Override
 *          public Response onCompleted(Response response) throws IOException &#123;
 *               // Do something
 *              return response;
 *          &#125;
 *
 *          @Override
 *          public void onThrowable(Throwable t) &#123;
 *          &#125;
 *      &#125;);
 *      Response response = f.get();
 *
 *      // We are just interested to retrieve the status code.
 *     Future<Integer> f = c.doGet("http://www.ning.com/", new AsyncHandler<Integer>() &#123;
 *
 *          @Override
 *          public Integer onCompleted(Response response) throws IOException &#123;
 *               // Do something
 *              return response.getStatusCode();
 *          &#125;
 *
 *          @Override
 *          public void onThrowable(Throwable t) &#123;
 *          &#125;
 *      &#125;);
 *      Integer statusCode = f.get();
 * }
 * The {@link AsyncHandler#onCompleted(com.ning.http.client.Response)} will be invoked once the http response has been fully read, which include
 * the http headers and the response body. Note that the entire response will be buffered in memory.
 * 
 * You can also have more control about the how the response is asynchronously processed by using a {@link AsyncStreamingHandler}
 * {@code
 *      AsyncHttpClient c = new AsyncHttpClient();
 *      Future<Response> f = c.doGet("http://www.ning.com/", new AsyncStreamingHandler() &#123;
 *
 *          @Override
 *          public Response onContentReceived(HttpContent content) throws ResponseComplete &#123;
 *              if (content instanceof HttpResponseHeaders) &#123;
 *                  // The headers have been read
 *                  // If you don't want to read the body, or stop processing the response
 *                  throw new ResponseComplete();
 *              &#125; else if (content instanceof HttpResponseBody) &#123;
 *                  HttpResponseBody b = (HttpResponseBody) content;
 *                  // Do something with the body. It may not been fully read yet.
 *                  if (b.isComplete()) &#123;
 *                      // The full response has been read.
 *                  &#125;
 *              &#125;
 *              return content.getResponse();
 *          &#125;
 *
 *          @Override
 *          public void onThrowable(Throwable t) &#123;
 *          &#125;
 *      &#125;);
 *      Response response = f.get();
 * }
 * From an {@link HttpContent}, you can asynchronously process the response headers and body and decide when to
 * stop the processing the response by throwing {@link AsyncStreamingHandler.ResponseComplete} at any moment. The returned
 * {@link Response} will be incomplete until {@link HttpResponseBody#isComplete()} return true, which means the
 * response has been fully read and buffered in memory.
 *
 * This class can also be used with the need of {@link AsyncHandler}</p>
 * {@code
 *      AsyncHttpClient c = new AsyncHttpClient();
 *      Future<Response> f = c.doGet(TARGET_URL);
 *      Response r = f.get();
 * }
 */
public class AsyncHttpClient {
  private final AsyncHttpProvider httpProvider;

  private final AsyncHttpClientConfig config, DEFAULT_MAX_CONNECTIONS_PER_HOST = Integer.getInteger("xn.httpClient.defaultMaxConnectionsPerHost", 2000);

  public AsyncHttpClient() {
    this(new AsyncHttpClientConfig.Builder().build());
  }

  public AsyncHttpClient(AsyncHttpClientConfig config) {
    this.config = config;
    this.httpProvider = new NettyAsyncHttpProvider(config);
  }

  public AsyncHttpClient(AsyncHttpProvider httpProvider) {
    this.config = new AsyncHttpClientConfig.Builder().build();
    this.httpProvider = httpProvider;
  }

  private final static AsyncHandler<Response> voidHandler = new AsyncHandler<Response>() {
    @Override public Response onCompleted(Response response) throws IOException {
      return response;
    }

    @Override public void onThrowable(Throwable t) {
      t.printStackTrace();
    }
  };

  public class BoundRequestBuilder extends RequestBuilderBase<BoundRequestBuilder> {
    private BoundRequestBuilder(RequestType type) {
      super(type);
    }

    private BoundRequestBuilder(Request prototype) {
      super(prototype);
    }

    public <T extends java.lang.Object> Future<T> execute(AsyncHandler<T> handler) throws IOException {
      return AsyncHttpClient.this.performRequest(build(), handler);
    }

    public Future<Response> execute() throws IOException {
      return AsyncHttpClient.this.performRequest(build(), voidHandler);
    }
  }

  public AsyncHttpProvider getProvider() {
    return httpProvider;
  }

  public void close() {
    httpProvider.close();
  }

  @Override protected void finalize() throws Throwable {
    close();
    super.finalize();
  }

  public AsyncHttpClientConfig getConfig() {
    return config;
  }

  public BoundRequestBuilder prepareGet(String url) {
    return new BoundRequestBuilder(RequestType.GET).setUrl(url);
  }

  public BoundRequestBuilder prepareHead(String url) {
    return new BoundRequestBuilder(RequestType.HEAD).setUrl(url);
  }

  public BoundRequestBuilder preparePost(String url) {
    return new BoundRequestBuilder(RequestType.POST).setUrl(url);
  }

  public BoundRequestBuilder preparePut(String url) {
    return new BoundRequestBuilder(RequestType.PUT).setUrl(url);
  }

  public BoundRequestBuilder prepareDelete(String url) {
    return new BoundRequestBuilder(RequestType.DELETE).setUrl(url);
  }

  public BoundRequestBuilder prepareRequest(Request request) {
    return new BoundRequestBuilder(request);
  }

  public <T extends java.lang.Object> Future<T> performRequest(Request request, AsyncHandler<T> handler) throws IOException {
    return httpProvider.handle(request, handler);
  }
}