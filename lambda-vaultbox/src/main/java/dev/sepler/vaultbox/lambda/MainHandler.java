package dev.sepler.vaultbox.lambda;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

import java.util.Map;

@Log4j2
@RequiredArgsConstructor
public class MainHandler implements RequestHandler<Map<String, String>, String> {

    @Override
    public String handleRequest(final Map<String, String> request, final Context context) {
        log.info("Received request: {}", request);
        return null;
    }

}
