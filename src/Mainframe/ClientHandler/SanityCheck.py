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
"""
Helper module which performs a normal sanity check by connecting to the given IP and port.
When receiving a socket timeout or connection refused error message, set the 'fine' variable to False to indicate the suspicious IP.
"""
import socket, logging

def checkIP(IP, port):
    """
    Checks a given IP on the given ports
    @type IP: string
    @param IP: the IP to check.
    @type ports: list
    @param ports: the ports to check.
    """
    results = []

    results.append({'port':port,'fine':True})
    failed = False
    sock = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
    sock.settimeout(2)
    logging.info("[NORMALCHECK] Checking port " + str(port) + " on IP " + IP)
    try:
        sock.connect((IP, int(port)))
    except socket.timeout:
        logging.info("[NORMALCHECK] Got connection timeout")
        failed = True
    except socket.error as error:
        logging.info("[NORMALCHECK] Got error " + error.strerror)
        if error.strerror == "Connection refused" or error.strerror == "Connection timed out":
            failed = True
    if failed:
        results[-1]['fine'] = False
    return results




