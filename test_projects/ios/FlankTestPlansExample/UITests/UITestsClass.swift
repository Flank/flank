import XCTest

class UITestsClass: XCTestCase {

    func test1_1() {
        XCTAssertEqual("", "")
    }
    
    func test1_2() {
        XCTAssertEqual("", "")
    }
    
    func test1_3() {
        XCTAssertEqual("", "")
    }
    
    func test1_ENLocale() {
        XCTAssert(Locale.current.identifier.contains("en"))
    }
    
    func test1_PLLocale() {
        XCTAssert(Locale.current.identifier.contains("pl"))
    }
}
