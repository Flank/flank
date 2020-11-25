import Foundation
import UIKit

class FTLGameLoopScenario {
    let scenarioNumber: Int?
    
    var scenarioName: String {
        if let n = scenarioNumber { return "\(n)" }
        return "default"
    }
    
    init?(url: URL) {
        guard let components = URLComponents(url: url, resolvingAgainstBaseURL: true) else {
            print("[FTLGameLoopScenario] Incorrect URL")
            return nil
        }
        guard components.scheme == "firebase-game-loop" else {
            print("[FTLGameLoopScenario] Incorrect URL scheme")
            return nil
        }
        
        if let queryItem = components.queryItems?.first(where: { $0.name == "scenario" })?.value, let scenarioNum = Int(queryItem) {
            self.scenarioNumber = scenarioNum
        } else {
            self.scenarioNumber = nil
        }
        
        print("[FTLGameLoopScenario] FTLGameLoopScenario initialized with scenario number: \(scenarioNumber ?? -1)")
    }
    
    func start() {
        print("[FTLGameLoopScenario] FTLGameLoopScenario started")
        DispatchQueue.main.asyncAfter(deadline: .now() + 2.0) {
            self.finishGameLoop()
        }
    }
    
    func finishGameLoop() {
        writeResults()
        print("[FTLGameLoopScenario] FTLGameLoopScenario finished")
        let url = URL(string: "firebase-game-loop-complete://")!
        UIApplication.shared.open(url)
    }
    
    func writeResults() {
        let text = "Greetings from game loops!"
        let fileName = "results_scenario_\(scenarioName).txt"
        let fileManager = FileManager.default
        
        print("[FTLGameLoopScenario] Saving results to: \(fileName)")
        do {
            let docs = try fileManager.url(for: .documentDirectory,
                                           in: .userDomainMask,
                                           appropriateFor: nil,
                                           create: true)
            let resultsDir = docs.appendingPathComponent("GameLoopResults")
            try fileManager.createDirectory(at: resultsDir, withIntermediateDirectories: true, attributes: nil)
            let fileURL = resultsDir.appendingPathComponent(fileName)
            try text.write(to: fileURL, atomically: false, encoding: .utf8)
        } catch {
        }
    }
}
