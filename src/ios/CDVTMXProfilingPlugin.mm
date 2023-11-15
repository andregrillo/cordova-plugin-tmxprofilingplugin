- (void) profile: (CDVInvokedUrlCommand*)command
{
    [self initTD];
    TMXProfileHandle *profileHandle = [[TMXProfiling sharedInstance]
                                       profileDeviceUsing:@{
                                                                     // (OPTIONAL) Assign some custom attributes to be included with the profiling information
                                            THMCustomAttributes: customAttributes
                                        }
                                       callbackBlock:^(NSDictionary *result)
                                       {
                                           THMStatusCode statusCode = [[result valueForKey:TMXProfileStatus] integerValue];
                                       }];
}
- (void) initTD
{
    _sessionID = nil;
    _timeout   = @10;
    _connections = [[TMXProfilingConnections alloc] init];
     
    [[TMXProfiling sharedInstance] configure:@{
                                               TMXOrgID : "saawfby5",
                                               TMXFingerprintServer : "https://h.online-metrix.net/fp/tags.js",
                                               TMXProfilingConnectionsInstance:_connections        
    }];
}