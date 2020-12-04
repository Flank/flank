import XCTest

class SecondUITestsClass: XCTestCase {

    func test2_1() {
        XCTAssertEqual("", "")
    }
    
    func test2_2() {
        XCTAssertEqual("", "")
    }
    
    func test2_3() {
        XCTAssertEqual("", "")
    }
    
    func test2_ENLocale() {
        XCTAssert(Locale.current.identifier.contains("en"))
    }
    
    func test2_PLLocale() {
        XCTAssert(Locale.current.identifier.contains("pl"))
    }
}
