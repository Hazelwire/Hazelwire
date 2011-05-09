import unittest
from tests.test_db import DatabaseHandlerTestCase
from tests.test_flagdistrib import FlagDistributionTestCase
from tests.test_manifestparser import ManifestParserTestCase

db_suite = unittest.TestLoader().loadTestsFromTestCase(DatabaseHandlerTestCase)
flagdistrib_suite = unittest.TestLoader().loadTestsFromTestCase(FlagDistributionTestCase)
manifestparser_suite = unittest.TestLoader().loadTestsFromTestCase(ManifestParserTestCase)

all_tests = unittest.TestSuite([db_suite, flagdistrib_suite, manifestparser_suite])
unittest.TextTestRunner(verbosity=2).run(all_tests)
