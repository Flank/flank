//
//  AppDelegate.swift
//  flankgameloop
//
//  Created by Axel Zuziak on 12/11/2020.
//

import UIKit

@main
class AppDelegate: UIResponder, UIApplicationDelegate {
    
    var window: UIWindow?
    
    
    func application(_ application: UIApplication, didFinishLaunchingWithOptions launchOptions: [UIApplication.LaunchOptionsKey: Any]?) -> Bool {
        return true
    }
    
    func application(_ app: UIApplication, open url: URL, options: [UIApplication.OpenURLOptionsKey : Any] = [:]) -> Bool {
        let gameLoop = FTLGameLoopScenario(url: url)
        gameLoop?.start()
        
        return true
    }
}

