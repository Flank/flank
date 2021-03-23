import 'package:integration_test/integration_test.dart';

import 'failed_test.dart';
import 'success_test.dart';

void main() {
  IntegrationTestWidgetsFlutterBinding.ensureInitialized();
  runFailedTest();
  runSuccessTests();
  runSuccessTests2();
  runSuccessTests3();
  runSuccessTests4();
  runSuccessTests5();
}
