//
//  ViewController.swift
//  FlankExample
//
//  Created by Axel Zuziak on 17/09/2020.
//  Copyright © 2020 gogoapps. All rights reserved.
//

import UIKit

class ViewController: UIViewController {

    func getDocumentsDirectory() -> URL {
        let paths = FileManager.default.urls(for: .documentDirectory, in: .userDomainMask)
        return paths[0]
    }

    override func viewDidLoad() {
        super.viewDidLoad()
        // Do any additional setup after loading the view.


        let str = "viewLoaded"
        let outputDirectory = getDocumentsDirectory().appendingPathComponent("output")

        do {
            try FileManager.default.createDirectory(atPath: outputDirectory.path, withIntermediateDirectories: true, attributes: nil)
            let filename = outputDirectory.appendingPathComponent("test.txt")

            try str.write(to: filename, atomically: true, encoding: String.Encoding.utf8)

        } catch {
            print("error on create test file")
        }
    }
}

