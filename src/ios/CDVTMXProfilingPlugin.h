#import <Cordova/CDVPlugin.h>
#import <TMXProfilingConnections/TMXProfilingConnections.h>
#import <TMXProfilingConnections/TMXProfilingConnectionsProtocol.h>
#import <TMXProfiling/TMXProfiling.h>
@interface CDVTMXProfilingPlugin : CDVPlugin
- (void)profile: (CDVInvokedUrlCommand*)command;
- (void)initTD;
@end