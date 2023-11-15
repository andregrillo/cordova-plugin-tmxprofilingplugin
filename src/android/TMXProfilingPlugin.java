import android.util.Log;
import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaPlugin;
import org.json.JSONArray;
import org.json.JSONException;

public class TrustDefenderPlugin extends CordovaPlugin {
   @Override
    public boolean execute(String action, JSONArray args, final CallbackContext callbackContext) {
        try {
            // Parse the OrgId and FPServer from the args JSONArray
            String orgId = args.getString(0);
            String fpServer = args.getString(1);

            // Use the parsed arguments to configure TMXProfiling
            TMXProfilingConnections tmxConn = new TMXProfilingConnections();
            TMXConfig config = new TMXConfig().setOrgId(orgId)
                    .setFPServer(fpServer)
                    .setContext(this.cordova.getActivity().getApplicationContext())
                    .setDisableInitPackageScan(false)
                    .setDisableProfilePackageScan(false)
                    .setProfilingConnections(tmxConn);

            TMXProfiling.getInstance().init(config);
            doProfile();
            callbackContext.success(); // Signal success to the Cordova callback
        } catch (JSONException e) {
            callbackContext.error("JSON argument parsing error: " + e.getMessage());
            return false;
        } catch (Exception e) {
            callbackContext.error("Unexpected error: " + e.getMessage());
            return false;
        }
        return true; // if the action was handled
    }
}

private class CompletionNotifier implements TMXEndNotifier {
    /**
     * This gets called when the profiling has finished.
     * We have to be careful here because we are not going to be called on the UI thread, and
     * if we want to update UI elements we can only do it from the UI thread.
     */
    @Override
    public void complete(TMXProfilingHandle.Result result) {
        //Get the session id to use in API call (AKA session query)
        m_sessionID = result.getSessionID();
        Log.i("Plugin", "Profile completed with: " + result.getStatus().toString()+ " - " + result.getStatus().getDesc());
        /*
         * Profiling is complete, so login can proceed when the Login button is clicked.
         */
        setProfileFinished(true);
        /*
         * The Login button is clicked before the profiling is finished, therefore we should login
         * */
        if(isLoginClicked()) {
            login();
        }
    }
}