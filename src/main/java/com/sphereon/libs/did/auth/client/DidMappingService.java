package com.sphereon.libs.did.auth.client;

import com.sphereon.libs.did.auth.client.exceptions.UserNotFoundException;
import com.sphereon.libs.did.auth.client.model.UserInfo;
import com.sphereon.sdk.did.mapping.api.DidMapControllerApi;
import com.sphereon.sdk.did.mapping.handler.ApiException;
import com.sphereon.sdk.did.mapping.model.DidInfo;
import com.sphereon.sdk.did.mapping.model.DidMappingResponse;

public class DidMappingService {
    private final DidMapControllerApi didMapControllerApi;

    public DidMappingService(DidMapControllerApi didMapControllerApi) {
        this.didMapControllerApi = didMapControllerApi;
    }

    public UserInfo getUserInfo(String applicationId, String userId) throws UserNotFoundException {
        try {
            DidMappingResponse didMappingResponse = didMapControllerApi.getDidMap(applicationId, userId);
            if (didMappingResponse.getDidMaps() != null && didMappingResponse.getDidMaps().size() > 0) {
                DidInfo didInfo = didMappingResponse.getDidMaps().get(0).getDidInfo();
                return new UserInfo(didInfo.getDid(), didInfo.getBoxPub(), didInfo.getPushToken());
            } else {
                throw new RuntimeException("DidMappingResponse was null or empty when returned from did-mapping-ms for user: " + userId);
            }
        } catch (ApiException | RuntimeException e) {
            throw new UserNotFoundException(e.getMessage());
        }
    }
}
