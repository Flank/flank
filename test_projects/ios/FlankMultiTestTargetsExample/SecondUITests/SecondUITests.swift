import XCTest

class SecondUITests: XCTestCase {

    func test11() {
        XCTAssertEqual("", "")
    }
    
    func test12() {
        XCTAssertEqual("", "")
    }
    
    func test13() {
        XCTAssertEqual("", "")
    }
    
    func test2ENLocale() {
        XCTAssert(Locale.current.identifier.contains("en"))
    }
    
    func test2PLLocale() {
        XCTAssert(Locale.current.identifier.contains("pl"))
    }
}
