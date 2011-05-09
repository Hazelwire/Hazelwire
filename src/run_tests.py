import unittest
from tests.test_db import DatabaseHandlerTestCase
from tests.test_flagdistrib import FlagDistributionTestCase

db_suite = unittest.TestLoader().loadTestsFromTestCase(DatabaseHandlerTestCase)
flagdistrib_suite = unittest.TestLoader().loadTestsFromTestCase(FlagDistributionTestCase)

all_tests = unittest.TestSuite([db_suite, flagdistrib_suite])
unittest.TextTestRunner(verbosity=2).run(all_tests)
