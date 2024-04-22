package fr.kinjer.vertxutils.utils;

import fr.kinjer.vertxutils.module.request.IRequest;
import fr.kinjer.vertxutils.module.request.Request;
import fr.kinjer.vertxutils.module.request.Response;

import java.util.HashSet;
import java.util.Set;

public class CheckUtil {
    public static void checkDuplicateSubRequest(IRequest<Response> request) {
        if (!request.getSubRequests().isEmpty()) {
            String parentPath = request.getPath();
            Set<String> paths = new HashSet<>();
            paths.add(parentPath);
            for (IRequest<Response> subRequest : request.getSubRequests()) {
                String subPath = subRequest.getPath();
                if (paths.contains(subPath)) {
                    throw new IllegalArgumentException("SubRequest path '" + subPath + "' already exists");
                }
                paths.add(subPath);
            }
        }
    }
}
