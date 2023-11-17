package com.threatmetrix.cordova.plugin;

import android.util.Log;
import com.lexisnexisrisk.threatmetrix.TMXConfig;
import com.lexisnexisrisk.threatmetrix.TMXEndNotifier;
import com.lexisnexisrisk.threatmetrix.TMXProfiling;
import com.lexisnexisrisk.threatmetrix.TMXProfilingHandle;
import com.lexisnexisrisk.threatmetrix.TMXProfilingOptions;
import com.lexisnexisrisk.threatmetrix.TMXScanEndNotifier;
import com.lexisnexisrisk.threatmetrix.TMXStatusCode;
import com.lexisnexisrisk.threatmetrix.tmxprofilingconnections.TMXProfilingConnections;
import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaPlugin;
import org.json.JSONArray;
import org.json.JSONException;
import java.util.ArrayList;

public class TMXProfilingPlugin extends CordovaPlugin {

    @Override
    public boolean execute(String action, JSONArray args, final CallbackContext callbackContext) {
        try {
            if ("init".equals(action)) {
                return init(args, callbackContext);
            } else if ("doProfile".equals(action)) {
                doProfile(callbackContext);
                return true;
            }  else {
                callbackContext.error("Action not recognized.");
                return false;
            }
        } catch (JSONException e) {
            callbackContext.error("JSON argument parsing error: " + e.getMessage());
            return false;
        } catch (Exception e) {
            callbackContext.error("Unexpected error: " + e.getMessage());
            return false;
        }
    }

    private boolean init(JSONArray args, CallbackContext callbackContext) throws JSONException {
        // Check if the OrgId argument is provided and not null
        if (args.isNull(0)) {
            callbackContext.error("OrgId argument is missing or null.");
            return false;
        }

        // Check if the FPServer argument is provided and not null
        if (args.isNull(1)) {
            callbackContext.error("FPServer argument is missing or null.");
            return false;
        }

        // Parse the OrgId and FPServer from the args JSONArray
        String orgId = args.getString(0);
        String fpServer = args.getString(1);

        // Validate the parsed arguments to ensure they are not empty
        if (orgId.isEmpty()) {
            callbackContext.error("OrgId argument is empty.");
            return false;
        }
        if (fpServer.isEmpty()) {
            callbackContext.error("FPServer argument is empty.");
            return false;
        }

        // Use the parsed arguments to configure TMXProfiling
        TMXProfilingConnections tmxConn = new TMXProfilingConnections();
        TMXConfig config = new TMXConfig()
                .setOrgId(orgId)
                .setFPServer(fpServer)
                .setContext(this.cordova.getActivity().getApplicationContext())
                .setDisableInitPackageScan(false)
                .setDisableProfilePackageScan(false)
                .setRegisterForLocationServices(true)
                .setDisableLocSerOnBatteryLow(true)
                .setProfilingConnections(tmxConn);

        TMXProfiling.getInstance().init(config);
        callbackContext.success();
        return true;
    }

    private void doProfile(final CallbackContext callbackContext) {
        TMXProfilingOptions options = new TMXProfilingOptions().setCustomAttributes(new ArrayList<String>());

        TMXProfiling.getInstance().profile(options, new TMXEndNotifier() {
            @Override
            public void complete(TMXProfilingHandle.Result result) {
                // Log the profiling result
                Log.i("Plugin", "⭐️ Profiling is finished with result of " + result.getStatus() +
                        " session id is " + result.getSessionID());

                // Check the status of the profiling
                TMXStatusCode statusCode = result.getStatus();
                if (statusCode == TMXStatusCode.TMX_OK) {
                    // Profiling was successful, return the session ID
                    callbackContext.success(result.getSessionID());

                    // Start the scan packages
                    TMXProfiling.getInstance().scanPackages();
                } else {
                    // Profiling failed, return the error description
                    callbackContext.error("Profiling failed with status: " + statusCode.getDesc());
                }
            }
        });
    }
}