package org.sunbird.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sunbird.exception.BaseException;
import org.sunbird.message.IResponseMessage;
import org.sunbird.models.SearchRequest;
import org.sunbird.response.Response;
import org.sunbird.util.HttpClientUtil;
import org.sunbird.util.JsonKey;
import org.sunbird.util.helper.PropertiesCache;

public class UserServiceImpl implements UserService {

  Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);

  private static String userServiceSearchUrl;
  private static String userServiceBaseUrl;
  private static String userServiceSystemSettingUrl;
  private static String userServiceOrgReadUrl;

  private static UserService userService = null;
  private static ObjectMapper objectMapper = new ObjectMapper();

  static {
    userServiceBaseUrl = System.getenv(JsonKey.USER_SERVICE_BASE_URL);
    userServiceSearchUrl = System.getenv(JsonKey.USER_SERVICE_SEARCH_URL);
    userServiceSystemSettingUrl = System.getenv(JsonKey.USER_SERVICE_SYSTEM_SETTING_URL);
    userServiceOrgReadUrl = System.getenv(JsonKey.USER_SERVICE_ORG_READ_URL);
    if (StringUtils.isBlank(userServiceBaseUrl)) {
      userServiceBaseUrl = PropertiesCache.getInstance().getProperty(JsonKey.USER_SERVICE_BASE_URL);
    }
    if (StringUtils.isBlank(userServiceSearchUrl)) {
      userServiceSearchUrl =
          PropertiesCache.getInstance().getProperty(JsonKey.USER_SERVICE_SEARCH_URL);
    }
    if (StringUtils.isBlank(userServiceSystemSettingUrl)) {
      userServiceSystemSettingUrl =
          PropertiesCache.getInstance().getProperty(JsonKey.USER_SERVICE_SYSTEM_SETTING_URL);
    }
    if (StringUtils.isBlank(userServiceOrgReadUrl)) {
      userServiceOrgReadUrl =
          PropertiesCache.getInstance().getProperty(JsonKey.USER_SERVICE_ORG_READ_URL);
    }
  }

  public static UserService getInstance() {
    if (userService == null) {
      userService = new UserServiceImpl();
    }
    return userService;
  }

  /**
   * Fetch user details with user ids from Platform service
   *
   * @param userIds
   * @return
   * @throws BaseException
   */
  @Override
  public Response searchUserByIds(List<String> userIds) throws BaseException {
    Response responseObj = new Response();
    Map<String, String> requestHeader = new HashMap<>();
    getUpdatedRequestHeader(requestHeader);
    try {
      SearchRequest searchRequest = createUserSearchRequest(userIds);
      String searchJsonStrReq = objectMapper.writeValueAsString(searchRequest);
      String response =
          HttpClientUtil.post(
              userServiceBaseUrl + userServiceSearchUrl, searchJsonStrReq, requestHeader);
      getResponseObject(responseObj, response);
    } catch (JsonProcessingException ex) {
      logger.error("Error while fetching user details through user service" + ex.getMessage());
      throw new BaseException(IResponseMessage.SERVER_ERROR, IResponseMessage.INTERNAL_ERROR);
    }
    return responseObj;
  }

  @Override
  public Response getSystemSettings() throws BaseException {
    Response responseObj = new Response();
    Map<String, String> requestHeader = new HashMap<>();
    getUpdatedRequestHeader(requestHeader);
    try {

      String response =
          HttpClientUtil.get(userServiceBaseUrl + userServiceSystemSettingUrl, requestHeader);
      getResponseObject(responseObj, response);
    } catch (JsonProcessingException ex) {
      logger.error("Error while fetching system setting through user service" + ex.getMessage());
      throw new BaseException(IResponseMessage.SERVER_ERROR, IResponseMessage.INTERNAL_ERROR);
    }
    return responseObj;
  }

  @Override
  public Response getOrganisationDetails(String orgId) throws BaseException {
    Response responseObj = new Response();
    SearchRequest readRequest = new SearchRequest();
    readRequest.getRequest().put(JsonKey.ORGANISATION_ID, orgId);

    Map<String, String> requestHeader = new HashMap<>();
    getUpdatedRequestHeader(requestHeader);
    try {
      String response =
          HttpClientUtil.post(
              userServiceBaseUrl + userServiceOrgReadUrl,
              objectMapper.writeValueAsString(readRequest),
              requestHeader);
      getResponseObject(responseObj, response);
    } catch (Exception ex) {
      logger.error("Error while fetching org details through user service" + ex.getMessage());
      throw new BaseException(IResponseMessage.SERVER_ERROR, IResponseMessage.INTERNAL_ERROR);
    }
    return responseObj;
  }

  private void getResponseObject(Response responseObj, String response)
      throws JsonProcessingException {
    if (StringUtils.isNotBlank(response)) {
      Map<String, Object> responseMap = objectMapper.readValue(response, Map.class);
      responseObj.putAll((Map<String, Object>) responseMap.get(JsonKey.RESULT));
    } else {
      logger.error("Empty response from the user service:" + response);
    }
  }

  private SearchRequest createUserSearchRequest(List<String> userIds) {
    SearchRequest searchRequest = new SearchRequest();
    Map<String, Object> request = new HashMap<>();
    Map<String, Object> filters = new HashMap<>();
    filters.put(JsonKey.ID, userIds);
    request.put(JsonKey.FILTERS, filters);
    searchRequest.setRequest(request);
    return searchRequest;
  }
}
