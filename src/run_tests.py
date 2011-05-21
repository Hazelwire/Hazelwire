import unittest
from tests.test_db import DatabaseHandlerTestCase
from tests.test_flagdistrib import FlagDistributionTestCase
from tests.test_manifestparser import ManifestParserTestCase
from tests.test_sanitycheck import SanityCheckTestCase

db_suite = unittest.TestLoader().loadTestsFromTestCase(DatabaseHandlerTestCase)
flagdistrib_suite = unittest.TestLoader().loadTestsFromTestCase(FlagDistributionTestCase)
manifestparser_suite = unittest.TestLoader().loadTestsFromTestCase(ManifestParserTestCase)
sanitycheck_suite = unittest.TestLoader().loadTestsFromTestCase(SanityCheckTestCase)

all_tests = unittest.TestSuite([db_suite, flagdistrib_suite, manifestparser_suite, sanitycheck_suite])
unittest.TextTestRunner(verbosity=2).run(all_tests)
