package com.threatmetrix.cordova.plugin;

import android.util.Log;
import com.lexisnexisrisk.threatmetrix.TMXConfig;
import com.lexisnexisrisk.threatmetrix.TMXEndNotifier;
import com.lexisnexisrisk.threatmetrix.TMXProfiling;
import com.lexisnexisrisk.threatmetrix.TMXProfilingHandle;
import com.lexisnexisrisk.threatmetrix.TMXProfilingOptions;
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
            // Parse the OrgId and FPServer from the args JSONArray
            //Falta testar as variáveis de entrada
            String orgId = args.getString(0);
            String fpServer = args.getString(1);
            //Faltam parâmetros aqui

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

            //callbackContext.success(); // Signal success to the Cordova callback
        } catch (JSONException e) {
            callbackContext.error("JSON argument parsing error: " + e.getMessage());
            return false;
        } catch (Exception e) {
            callbackContext.error("Unexpected error: " + e.getMessage());
            return false;
        }
        return true;
    }

    void doProfile() {
        //Verificar o que deve ser passado no ArrayList
       TMXProfilingOptions options = new TMXProfilingOptions().setCustomAttributes(new ArrayList<>());
       // Fire off the profiling request. We could use a more complex request,
       // but the minimum works fine for our purposes.
       TMXProfilingHandle profilingHandle = TMXProfiling.getInstance().profile(options, new CompletionNotifier());
   }
}

class CompletionNotifier implements TMXEndNotifier
{
    /**
     * This gets called when the profiling has finished.
     * We have to be careful here because we are not going to be called on the UI thread, and
     * if we want to update UI elements we can only do it from the UI thread.
     */
    @Override
    public void complete(TMXProfilingHandle.Result result)
    {
        //Get the session id to use in API call (AKA session query)
        String m_sessionID = result.getSessionID();
        Log.i("Plugin", "Profile completed with: " + result.getStatus().toString()+ " - " + result.getStatus().getDesc());
        /*
         * Profiling is complete, so login can proceed when the Login button is clicked.
         */
        //setProfileFinished(true);

        //CHAMAR O CALLBACK

//        /*
//         * The Login button is clicked before the profiling is finished, therefore we should login
//         * */
//        if(isLoginClicked())
//        {
//            login();
//        }
    }
}