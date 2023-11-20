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
import java.util.concurrent.TimeUnit;

public class TMXProfilingPlugin extends CordovaPlugin {

    @Override
    public boolean execute(String action, JSONArray args, final CallbackContext callbackContext) {
        try {
            if ("init".equals(action)) {
                return init(args, callbackContext);
            } else if ("doProfile".equals(action)) {
                doProfile(callbackContext);
                return true;
            } else if ("cancelProfile".equals(action)) {
                cancelProfile(callbackContext);
                return true;
            } else {
                callbackContext.error("Error: Action not recognized.");
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
            callbackContext.error("Error: OrgId argument is missing or null.");
            return false;
        }

        // Check if the FPServer argument is provided and not null
        if (args.isNull(1)) {
            callbackContext.error("Error: FPServer argument is missing or null.");
            return false;
        }

        // Parse the OrgId and FPServer from the args JSONArray
        String orgId = args.getString(0);
        String fpServer = args.getString(1);

        // Optional argument parsing
        boolean registerForLocationServices = args.optBoolean(2, true); // Default to true if not provided
        int screenOffTimeout = args.optInt(3, 180); // Default to 180 if not provided
        boolean disableLocSerOnBatteryLow = args.optBoolean(4, false); // Default to false if not provided
        int profileTimeout = args.optInt(5, 30); // Default to 30 if not provided
        boolean disableNonfatalLogs = args.optBoolean(6, false); // Default to false if not provided

        // Validate the parsed arguments to ensure they are not empty
        if (orgId.isEmpty()) {
            callbackContext.error("Error: OrgId argument is empty.");
            return false;
        }
        if (fpServer.isEmpty()) {
            callbackContext.error("Error: FPServer argument is empty.");
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
                .setRegisterForLocationServices(registerForLocationServices) //
                .setScreenOffTimeout(screenOffTimeout, TimeUnit.SECONDS) //
                .setDisableLocSerOnBatteryLow(disableLocSerOnBatteryLow) //
                .setProfileTimeout(profileTimeout,TimeUnit.SECONDS)
                .setProfilingConnections(tmxConn);

        if (disableNonfatalLogs) {
            config.disableNonfatalLogs();
        }

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
                    callbackContext.error("Error: Profiling failed with status " + statusCode.getDesc());
                }
            }
        });
    }

    private void cancelProfile(final CallbackContext callbackContext) {
        TMXProfilingHandle profilingHandle = TMXProfiling.getInstance().profile(new TMXEndNotifier(){
            @Override
            public void complete(TMXProfilingHandle.Result result)
            {
                Log.i("Plugin", "Profiling is finished with result of "+result.getStatus()+" session id is "+result.getSessionID());
                // Check the status of the profiling
                TMXStatusCode statusCode = result.getStatus();
                if (statusCode == TMXStatusCode.TMX_OK) {
                    callbackContext.success();
                } else {
                    // Cancel Profiling failed, return the error description
                    callbackContext.error("Error: Profiling failed with status " + statusCode.getDesc());
                }
            }
        });
        profilingHandle.cancel();
    }
}