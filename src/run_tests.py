# Copyright (c) 2011 The Hazelwire Team.
#     
# This file is part of Hazelwire.
# 
# Hazelwire is free software: you can redistribute it and/or modify
# it under the terms of the GNU General Public License as published by
# the Free Software Foundation, either version 3 of the License, or
# (at your option) any later version.
# 
# Hazelwire is distributed in the hope that it will be useful,
# but WITHOUT ANY WARRANTY; without even the implied warranty of
# MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
# GNU General Public License for more details.
# 
# You should have received a copy of the GNU General Public License
# along with Hazelwire.  If not, see <http://www.gnu.org/licenses/>.
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
