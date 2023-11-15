public class TrustDefenderPlugin extends CordovaPlugin {
   @Override
   public boolean execute(String action, JSONArray args,
       final CallbackContext callbackContext) {
             TMXProfilingConnections tmxConn=new TMXProfilingConnections();
             TMXConfig config = new TMXConfig().setOrgId("saawfby5")
                       .setFPServer("https://h.online-metrix.net/fp/tags.js")                  
                       .setContext(this.cordova.getActivity().getApplicationContext())
                       .setDisableInitPackageScan(false)
                       .setDisableProfilePackageScan(false)
                       .setProfilingConnections(tmxConn);
             TMXProfiling.getInstance().init(config);
             doProfile();
       }
   void doProfile()
   {
       TMXProfilingOptions options = new TMXProfilingOptions().setCustomAttributes(list);
       // Fire off the profiling request. We could use a more complex request,
       // but the minimum works fine for our purposes.
       TMXProfilingHandle profilingHandle = TMXProfiling.getInstance().profile(options,
               new CompletionNotifier());
   }
}

private class CompletionNotifier implements TMXEndNotifier
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
        m_sessionID = result.getSessionID();
        Log.i("Plugin", "Profile completed with: " + result.getStatus().toString()+ " - " + result.getStatus().getDesc());
        /*
         * Profiling is complete, so login can proceed when the Login button is clicked.
         */
        setProfileFinished(true);
        /*
         * The Login button is clicked before the profiling is finished, therefore we should login
         * */
        if(isLoginClicked())
        {
            login();
        }
    }
}