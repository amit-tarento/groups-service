package utils.module;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.util.Map;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sunbird.exception.BaseException;
import org.sunbird.message.IResponseMessage;
import org.sunbird.message.ResponseCode;
import org.sunbird.request.Request;
import org.sunbird.util.JsonKey;
import play.libs.Json;

public class RequestMapper {

  private ObjectMapper mapper = new ObjectMapper();
  private static Logger logger = LoggerFactory.getLogger(RequestMapper.class);

  public Request createSBRequest(play.mvc.Http.Request httpReq) {
    // Copy body

    JsonNode requestData = httpReq.body().asJson();
    if (requestData == null || requestData.isMissingNode()) {
      requestData = JsonNodeFactory.instance.objectNode();
    }

    // Copy headers
    try {
      ObjectNode headerData = Json.mapper().valueToTree(httpReq.getHeaders().toMap());
      ((ObjectNode) requestData).set("headers", headerData);

      Request request = Json.fromJson(requestData, Request.class);
      String contextStr = null;
      if(httpReq.attrs() != null && httpReq.attrs().containsKey(Attrs.CONTEXT)){
        contextStr = (String)httpReq.attrs().get(Attrs.CONTEXT);
      }
      if (StringUtils.isNotBlank(contextStr)) {
        Map<String, Object> contextObject = mapper.readValue(contextStr, Map.class);
        request.setContext((Map<String, Object>) contextObject.get(JsonKey.CONTEXT));
      }
      String userId = null;
      if(httpReq.attrs() != null && httpReq.attrs().containsKey(Attrs.USERID)){
        userId = (String)httpReq.attrs().get(Attrs.USERID);
      }
      logger.info(JsonKey.USER_ID + " in RequestMapper.createSBRequest(): " + userId);
      request.getContext().put(JsonKey.USER_ID, userId);

      String managedFor = null;
      if(httpReq.attrs() != null && httpReq.attrs().containsKey(Attrs.MANAGED_FOR)){
        managedFor = (String)httpReq.attrs().get(Attrs.MANAGED_FOR);
      }
      logger.info(JsonKey.MANAGED_FOR + " in RequestMapper.createSBRequest(): " + managedFor);
      request.getContext().put(JsonKey.MANAGED_FOR, managedFor);
      request.setPath(httpReq.path());

      return request;
    } catch (Exception ex) {
      logger.error("Error process set request context" + ex.getMessage());
      throw new BaseException(
          IResponseMessage.SERVER_ERROR,
          IResponseMessage.INTERNAL_ERROR,
          ResponseCode.SERVER_ERROR.getCode());
    }
  }
}
