Vulnerabilities matcher
=======================

:course:        Advanced Computer Networks (practicals)
:title:         WebCrawler - Vulnerabilities database matcher
:students:      Lukas Prokop, Saša Rošić, Robert Užar

Background
----------

This Java software takes the output of the `web-crawler-analysis`_ project
from the database and matches the data with the `vulnerabilities database`_
of NIST (NVD). FYI, we have taken the database ``nvdcve-2.0-2013.xml`` for
our matching.

Matches
-------

* 5123 vulnerabilities in NIST's XML file
* 69718 database entries for ``<meta name="generator">`` and ``X-Powered-By`` found

And the matches?

* 0 matches if no database entry would match any vulnerability
* 77734 *actual* matches found by us
* 357165314 (=5123*69718) matches if everything would match everything

Running the software
--------------------

A `MySQL`_ server must be running and the web-crawler-analysis database must
be stored in the database ``web-crawler``. You have to provide the
access credentials in the configuration file ``config.xml``.
Then you can start the Java program using ``./matcher vul_db.xml`` where
``vul_db.xml`` is some XML file by NIST.

The program will create a file ``matching_result.xml`` containing the matches.

Related
-------

Prior work by chille:
http://projects.chille.at/index.php/internet-security/55-web-crawler-security-analysis


best regards,
Lukas, Saša and Robert

.. _web-crawler-analysis: https://github.com/IAIK/web-crawler-analysis
.. _vulnerabilities database: http://nvd.nist.gov/
.. _MySQL: http://www.mysql.com/
