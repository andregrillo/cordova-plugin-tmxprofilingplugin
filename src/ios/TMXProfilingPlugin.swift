//
//  TMXProfilingPlugin.swift
//
//  Created by Andre Grillo on 18/12/2023.
//

import TMXProfiling
import TMXProfilingConnections

@objc
class TMXProfilingPlugin: CDVPlugin {
    
    var profile: TMXProfiling!
    var profileHandle: TMXProfileHandle!
    var sessionID = ""
    
    @objc(init:)
    func initialize(_ command: CDVInvokedUrlCommand) {
        var orgId: String?
        var fpServer: String?
        var registerForLocationServices = true
        //Android only - skipped
        //var screenOffTimeout = 180
        //var disableLocSerOnBatteryLow = false
        var profileTimeout = 30
        var disableNonfatalLogs = false
        
        if let orgIdValue = command.arguments[0] as? String, let fpServerValue = command.arguments[1] as? String {
            orgId = orgIdValue
            fpServer = fpServerValue
            
            if command.arguments.count > 2, let value = command.arguments[2] as? Bool {
                registerForLocationServices = value
            }
            //Android only - skipped
//            if command.arguments.count > 3, let value = command.arguments[3] as? Int {
//                screenOffTimeout = value
//            }
//            if command.arguments.count > 4, let value = command.arguments[4] as? Bool {
//                disableLocSerOnBatteryLow = value
//            }
            if command.arguments.count > 5, let value = command.arguments[5] as? Int {
                profileTimeout = value
            }
            if command.arguments.count > 6, let value = command.arguments[6] as? Bool {
                disableNonfatalLogs = value
            }
            
        } else {
            //ERROR: Missing input parameters
            print("🚨 ERROR: Missing input parameters")
            sendPluginResult(status: CDVCommandStatus_ERROR, message: "ERROR: Missing input parameters", callbackId: command.callbackId)
        }
        
        //Get a singleton instance of TMXProfiling
        if let profile = TMXProfiling.sharedInstance() {
            self.profile = profile
            
            profile.configure(configData:[
                                TMXOrgID              : orgId!,
                                TMXFingerprintServer  : fpServer!,
                                TMXProfileTimeout     : profileTimeout,
                                TMXDisableNonFatalLog : disableNonfatalLogs,
                                TMXLocationServices   : registerForLocationServices
                                ])
            sendPluginResult(status: CDVCommandStatus_OK, callbackId: command.callbackId)
        } else {
            print("🚨 ERROR: Could not initialize TMXProfiling singleton")
            sendPluginResult(status: CDVCommandStatus_ERROR, message: "ERROR: Could not initialize TMXProfiling singleton", callbackId: command.callbackId)
        }
    }
    
    @objc(doProfile:)
    func doProfile(_ command: CDVInvokedUrlCommand) {

        // Fire off the profiling request.
        self.profileHandle = profile.profileDevice(profileOptions: nil, callbackBlock:{ [self](result: [AnyHashable : Any]?) -> Void in

                let results:NSDictionary! = result! as NSDictionary
                let status:TMXStatusCode  = TMXStatusCode(rawValue:(results.value(forKey: TMXProfileStatus) as! NSNumber).intValue)!

                self.sessionID = results.value(forKey: TMXSessionID) as! String
                if(status == .ok)
                {
                    print("✅ No errors, profiling succeeded!")
                }

            let statusString: String =
                status == .ok                  ? "OK"                   :
                status == .networkTimeoutError ? "Timed out"            :
                status == .connectionError     ? "Connection Error"     :
                status == .hostNotFoundError   ? "Host Not Found Error" :
                status == .internalError       ? "Internal Error"       :
                status == .interruptedError    ? "Interrupted Error"    :
                "Other"
                print("⭐️ Profile completed with: \(statusString) and session ID: \(self.sessionID)")
            
            if status == .ok {
                sendPluginResult(status: CDVCommandStatus_OK, message: self.sessionID, callbackId: command.callbackId)
            } else {
                sendPluginResult(status: CDVCommandStatus_ERROR, message: "Error: \(statusString)", callbackId: command.callbackId)
            }
        })
    }
    
    @objc(cancelProfile:)
    func cancelProfile(_ command: CDVInvokedUrlCommand) {
        self.profileHandle?.cancel()
        sendPluginResult(status: CDVCommandStatus_OK, callbackId: command.callbackId)
    }
    
    func sendPluginResult(status: CDVCommandStatus, message: String = "", callbackId: String, keepCallback: Bool = false) {
        let pluginResult = CDVPluginResult(status: status, messageAs: message)
        pluginResult?.setKeepCallbackAs(keepCallback)
        self.commandDelegate!.send(pluginResult, callbackId: callbackId)
    }
}
    
    


