package org.sunbird.response;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/** This is a common response class for all the layer. All layer will send same response object. */
public class Response implements Serializable, Cloneable {

  private static final long serialVersionUID = -3773253896160786443L;
  private String id;
  private String ver;
  private String ts;
  private ResponseParams params;
  private int code = 200;
  private Map<String, Object> result = new HashMap<>();

  public Response() {}

  public Response(
      String id,
      String ver,
      String ts,
      Map<String, Object> result,
      ResponseParams params,
      int code) {
    this.id = id;
    this.ver = ver;
    this.result = result;
    this.params = params;
    this.code = code;
  }

  public Response(Map<String, Object> result, int code) {
    this.result = result;
    this.code = code;
  }

  public Response(int code) {
    this.code = code;
  }
  /**
   * This will provide request unique id.
   *
   * @return String
   */
  public String getId() {
    return id;
  }

  /**
   * set the unique id
   *
   * @param id String
   */
  public void setId(String id) {
    this.id = id;
  }

  /**
   * this will provide api version
   *
   * @return String
   */
  public String getVer() {
    return ver;
  }

  /**
   * set the api version
   *
   * @param ver String
   */
  public void setVer(String ver) {
    this.ver = ver;
  }

  /**
   * this will provide complete time value
   *
   * @return String
   */
  public String getTs() {
    return ts;
  }

  /**
   * set the time value
   *
   * @param ts String
   */
  public void setTs(String ts) {
    this.ts = ts;
  }

  /** @return Map<String, Object> */
  public Map<String, Object> getResult() {
    return result;
  }

  /**
   * @param key String
   * @return Object
   */
  public Object get(String key) {
    return result.get(key);
  }

  /**
   * @param key String
   * @param vo Object
   */
  public void put(String key, Object vo) {
    result.put(key, vo);
  }

  /** @param map Map<String, Object> */
  public void putAll(Map<String, Object> map) {
    result.putAll(map);
  }

  public boolean containsKey(String key) {
    return result.containsKey(key);
  }

  /**
   * This will provide response parameter object.
   *
   * @return ResponseParams
   */
  public ResponseParams getParams() {
    return params;
  }

  /**
   * set the response parameter object.
   *
   * @param params ResponseParams
   */
  public void setParams(ResponseParams params) {
    this.params = params;
  }

  /**
   * Set the response code for header.
   *
   * @param code ResponseCode
   */
  public void setResponseCode(int code) {
    this.code = code;
  }

  /**
   * get the response code
   *
   * @return String
   */
  public int getResponseCode() {
    return this.code;
  }

  public Response clone(Response response) {
    try {
      return (Response) response.clone();
    } catch (CloneNotSupportedException e) {
      return null;
    }
  }
}
