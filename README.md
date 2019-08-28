Siddhi IO WebSocket
======================================

[![Jenkins Build Status](https://wso2.org/jenkins/job/siddhi/job/siddhi-io-websocket/badge/icon)](https://wso2.org/jenkins/job/siddhi/job/siddhi-io-websocket/)
[![GitHub Release](https://img.shields.io/github/release/siddhi-io/siddhi-io-websocket.svg)](https://github.com/siddhi-io/siddhi-io-websocket/releases)
[![GitHub Release Date](https://img.shields.io/github/release-date/siddhi-io/siddhi-io-websocket.svg)](https://github.com/siddhi-io/siddhi-io-websocket/releases)
[![GitHub Open Issues](https://img.shields.io/github/issues-raw/siddhi-io/siddhi-io-websocket.svg)](https://github.com/siddhi-io/siddhi-io-websocket/issues)
[![GitHub Last Commit](https://img.shields.io/github/last-commit/siddhi-io/siddhi-io-websocket.svg)](https://github.com/siddhi-io/siddhi-io-websocket/commits/master)
[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](https://opensource.org/licenses/Apache-2.0)

The **siddhi-io-websocket extension** is an extension to <a target="_blank" href="https://wso2.github.io/siddhi">Siddhi</a>
  that allows to receive and publish events through WebSocket.

For information on <a target="_blank" href="https://siddhi.io/">Siddhi</a> and it's features refer <a target="_blank" href="https://siddhi.io/redirect/docs.html">Siddhi Documentation</a>. 

## Downloads
* Versions 3.x and above with group id `io.siddhi.extension.*` from <a target="_blank" href="https://mvnrepository.com/artifact/io.siddhi.extension.io.websocket/siddhi-io-websocket/">here</a>.
* Versions 2.x and lower with group id `org.wso2.extension.siddhi.` from  <a target="_blank" href="https://mvnrepository.com/artifact/org.wso2.extension.siddhi.io.websocket/siddhi-io-websocket">here</a>.

## Latest API Docs 

Latest API Docs is <a target="_blank" href="https://siddhi-io.github.io/siddhi-io-websocket/api/2.0.0">2.0.0</a>.

## Features

* <a target="_blank" href="https://siddhi-io.github.io/siddhi-io-websocket/api/2.0.0/#websocket-sink">websocket</a> *<a target="_blank" href="http://siddhi.io/documentation/siddhi-5.x/query-guide-5.x/#sink">(Sink)</a>*<br><div style="padding-left: 1em;"><p>A Siddhi application can be configured to publish events via the Websocket transport by adding the @Sink(type = 'websocket') annotation at the top of an event stream definition.</p></div>
* <a target="_blank" href="https://siddhi-io.github.io/siddhi-io-websocket/api/2.0.0/#websocket-server-sink">websocket-server</a> *<a target="_blank" href="http://siddhi.io/documentation/siddhi-5.x/query-guide-5.x/#sink">(Sink)</a>*<br><div style="padding-left: 1em;"><p>A Siddhi application can be configured to publish events via the WebSocket transport by adding the @Sink(type = 'websocket-server') annotation at the top of an event stream definition.</p></div>
* <a target="_blank" href="https://siddhi-io.github.io/siddhi-io-websocket/api/2.0.0/#websocket-source">websocket</a> *<a target="_blank" href="http://siddhi.io/documentation/siddhi-5.x/query-guide-5.x/#source">(Source)</a>*<br><div style="padding-left: 1em;"><p>A Siddhi application can be configured to receive events via the WebSocket by adding the @Source(type = 'websocket') annotation at the top of an event stream definition.<br>When this is defined the associated stream will receive events from the WebSocket server on the url defined in the system.</p></div>
* <a target="_blank" href="https://siddhi-io.github.io/siddhi-io-websocket/api/2.0.0/#websocket-server-source">websocket-server</a> *<a target="_blank" href="http://siddhi.io/documentation/siddhi-5.x/query-guide-5.x/#source">(Source)</a>*<br><div style="padding-left: 1em;"><p>A Siddhi application can be configured to receive events via the WebSocket by adding the @Source(type = 'websocket-server') annotation at the top of an event stream definition.</p></div>

## Dependencies

There are no other dependencies needed for this extension.

## Installation

For installing this extension on various siddhi execution environments refer Siddhi documentation section on <a target="_blank" href="https://siddhi.io/redirect/add-extensions.html">adding extensions</a>.

# Support and Contribution

* We encourage users to ask questions and get support via <a target="_blank" href="https://stackoverflow.com/questions/tagged/siddhi">StackOverflow</a>, make sure to add the `siddhi` tag to the issue for better response.
* If you find any issues related to the extension please report them on <a target="_blank" href="https://github.com/siddhi-io/siddhi-execution-string/issues">the issue tracker</a>.
* For production support and other contribution related information refer <a target="_blank" href="https://siddhi.io/community/">Siddhi Community</a> documentation.
