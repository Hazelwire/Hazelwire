import unittest
from tests.test_db import DatabaseHandlerTestCase

suite = unittest.TestLoader().loadTestsFromTestCase(DatabaseHandlerTestCase)
unittest.TextTestRunner(verbosity=2).run(suite)
