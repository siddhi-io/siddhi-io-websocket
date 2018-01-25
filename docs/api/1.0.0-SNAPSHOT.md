# API Docs - v1.0.0-SNAPSHOT

## Sink

### websocket *<a target="_blank" href="https://wso2.github.io/siddhi/documentation/siddhi-4.0/#sink">(Sink)</a>*

<p style="word-wrap: break-word">A Siddhi application can be configured to publish events via the Websocket transport by adding the @Sink(type = ‘websocket’) annotation at the top of an event stream definition.</p>

<span id="syntax" class="md-typeset" style="display: block; font-weight: bold;">Syntax</span>
```
@sink(type="websocket", url="<STRING>", sub.protocol="<STRING>", headers="<STRING>", idle.timeout="<INT>", @map(...)))
```

<span id="query-parameters" class="md-typeset" style="display: block; color: rgba(0, 0, 0, 0.54); font-size: 12.8px; font-weight: bold;">QUERY PARAMETERS</span>
<table>
    <tr>
        <th>Name</th>
        <th style="min-width: 20em">Description</th>
        <th>Default Value</th>
        <th>Possible Data Types</th>
        <th>Optional</th>
        <th>Dynamic</th>
    </tr>
    <tr>
        <td style="vertical-align: top">url</td>
        <td style="vertical-align: top; word-wrap: break-word">The URL of the remote endpoint.<br>The url scheme should be either ‘ws’ or ‘wss’.</td>
        <td style="vertical-align: top"></td>
        <td style="vertical-align: top">STRING</td>
        <td style="vertical-align: top">No</td>
        <td style="vertical-align: top">No</td>
    </tr>
    <tr>
        <td style="vertical-align: top">sub.protocol</td>
        <td style="vertical-align: top; word-wrap: break-word">The negotiable sub-protocol if server is asking for it.<br>The sub.protocol should adhere to <code>subprotocol1, subprotocol2,...</code> format.</td>
        <td style="vertical-align: top">null</td>
        <td style="vertical-align: top">STRING</td>
        <td style="vertical-align: top">Yes</td>
        <td style="vertical-align: top">No</td>
    </tr>
    <tr>
        <td style="vertical-align: top">headers</td>
        <td style="vertical-align: top; word-wrap: break-word">Any specific headers which need to send to the server.<br>The headers should adhere to <code>'key1:value1', 'key2:value2',...</code> format.</td>
        <td style="vertical-align: top">null</td>
        <td style="vertical-align: top">STRING</td>
        <td style="vertical-align: top">Yes</td>
        <td style="vertical-align: top">No</td>
    </tr>
    <tr>
        <td style="vertical-align: top">idle.timeout</td>
        <td style="vertical-align: top; word-wrap: break-word">Idle timeout of the connection</td>
        <td style="vertical-align: top">-1</td>
        <td style="vertical-align: top">INT</td>
        <td style="vertical-align: top">Yes</td>
        <td style="vertical-align: top">No</td>
    </tr>
</table>

<span id="examples" class="md-typeset" style="display: block; font-weight: bold;">Examples</span>
<span id="example-1" class="md-typeset" style="display: block; color: rgba(0, 0, 0, 0.54); font-size: 12.8px; font-weight: bold;">EXAMPLE 1</span>
```
@Sink(type = ‘websocket’, url = 'ws://localhost:8025/abc', 
   @map(type='xml'))
define stream Foo (attribute1 string, attribute2 int);
```
<p style="word-wrap: break-word">A sink of type 'websocket' has been defined.<br>All events arriving at Foo stream via websocket will be sent to the url ws://localhost:8025/abc.</p>

### websocket-server *<a target="_blank" href="https://wso2.github.io/siddhi/documentation/siddhi-4.0/#sink">(Sink)</a>*

<p style="word-wrap: break-word">A Siddhi application can be configured to publish events via the WebSocket transport by adding the @Sink(type = ‘websocket-server’) annotation at the top of an event stream definition.</p>

<span id="syntax" class="md-typeset" style="display: block; font-weight: bold;">Syntax</span>
```
@sink(type="websocket-server", host="<STRING>", port="<STRING>", sub.protocol="<STRING>", idle.timeout="<INT>", tls.enabled="<BOOL>", keystore.path="<STRING>", keystore.password="<STRING>", @map(...)))
```

<span id="query-parameters" class="md-typeset" style="display: block; color: rgba(0, 0, 0, 0.54); font-size: 12.8px; font-weight: bold;">QUERY PARAMETERS</span>
<table>
    <tr>
        <th>Name</th>
        <th style="min-width: 20em">Description</th>
        <th>Default Value</th>
        <th>Possible Data Types</th>
        <th>Optional</th>
        <th>Dynamic</th>
    </tr>
    <tr>
        <td style="vertical-align: top">host</td>
        <td style="vertical-align: top; word-wrap: break-word">host of the WebSocket server</td>
        <td style="vertical-align: top"></td>
        <td style="vertical-align: top">STRING</td>
        <td style="vertical-align: top">No</td>
        <td style="vertical-align: top">No</td>
    </tr>
    <tr>
        <td style="vertical-align: top">port</td>
        <td style="vertical-align: top; word-wrap: break-word">port of the WebSocket server</td>
        <td style="vertical-align: top"></td>
        <td style="vertical-align: top">STRING</td>
        <td style="vertical-align: top">No</td>
        <td style="vertical-align: top">No</td>
    </tr>
    <tr>
        <td style="vertical-align: top">sub.protocol</td>
        <td style="vertical-align: top; word-wrap: break-word">Sub-Protocols which are allowed by the service.<br>The sub.protocol should adhere to <code>subprotocol1, subprotocol2,...</code> format.</td>
        <td style="vertical-align: top">null</td>
        <td style="vertical-align: top">STRING</td>
        <td style="vertical-align: top">Yes</td>
        <td style="vertical-align: top">No</td>
    </tr>
    <tr>
        <td style="vertical-align: top">idle.timeout</td>
        <td style="vertical-align: top; word-wrap: break-word">Idle timeout of the connection. If the idle.timeout = '-1' then the timer is disabled.</td>
        <td style="vertical-align: top">-1</td>
        <td style="vertical-align: top">INT</td>
        <td style="vertical-align: top">Yes</td>
        <td style="vertical-align: top">No</td>
    </tr>
    <tr>
        <td style="vertical-align: top">tls.enabled</td>
        <td style="vertical-align: top; word-wrap: break-word">This parameter specifies whether a secure connection is enabled or not. When this parameter is set to <code>true</code>, the <code>keystore.path</code> and the <code>keystore.password</code> parameters are initialized.</td>
        <td style="vertical-align: top">false</td>
        <td style="vertical-align: top">BOOL</td>
        <td style="vertical-align: top">Yes</td>
        <td style="vertical-align: top">No</td>
    </tr>
    <tr>
        <td style="vertical-align: top">keystore.path</td>
        <td style="vertical-align: top; word-wrap: break-word">The file path to the location of the keystore. If a custom keystore is not specified, then the system uses the default keystore file - wso2carbon.jks in the <code>${carbon.home}/resources/security</code> directory.</td>
        <td style="vertical-align: top">${carbon.home}/resources/security/wso2carbon.jks</td>
        <td style="vertical-align: top">STRING</td>
        <td style="vertical-align: top">Yes</td>
        <td style="vertical-align: top">No</td>
    </tr>
    <tr>
        <td style="vertical-align: top">keystore.password</td>
        <td style="vertical-align: top; word-wrap: break-word">The password for the keystore. A custom password can be specified if required. If no custom password is specified, then the system uses <code>wso2carbon</code> as the default password.</td>
        <td style="vertical-align: top">wso2carbon</td>
        <td style="vertical-align: top">STRING</td>
        <td style="vertical-align: top">Yes</td>
        <td style="vertical-align: top">No</td>
    </tr>
</table>

<span id="examples" class="md-typeset" style="display: block; font-weight: bold;">Examples</span>
<span id="example-1" class="md-typeset" style="display: block; color: rgba(0, 0, 0, 0.54); font-size: 12.8px; font-weight: bold;">EXAMPLE 1</span>
```
@Sink(type = ‘websocket-server’, host='localhost', port='9025', 
   @map(type='xml'))
define stream Foo (attribute1 string, attribute2 int);
```
<p style="word-wrap: break-word">A sink of type 'websocket-server' has been defined.<br>All events arriving at Foo stream via websocket-server will be sent to the url ws://localhost:9025/abc.</p>

## Source

### websocket *<a target="_blank" href="https://wso2.github.io/siddhi/documentation/siddhi-4.0/#source">(Source)</a>*

<p style="word-wrap: break-word">A Siddhi application can be configured to receive events via the WebSocket by adding the @Source(type = ‘websocket’) annotation at the top of an event stream definition.<br>When this is defined the associated stream will receive events from the WebSocket server on the url defined in the system.</p>

<span id="syntax" class="md-typeset" style="display: block; font-weight: bold;">Syntax</span>
```
@source(type="websocket", url="<STRING>", sub.protocol="<STRING>", headers="<STRING>", idle.timeout="<INT>", @map(...)))
```

<span id="query-parameters" class="md-typeset" style="display: block; color: rgba(0, 0, 0, 0.54); font-size: 12.8px; font-weight: bold;">QUERY PARAMETERS</span>
<table>
    <tr>
        <th>Name</th>
        <th style="min-width: 20em">Description</th>
        <th>Default Value</th>
        <th>Possible Data Types</th>
        <th>Optional</th>
        <th>Dynamic</th>
    </tr>
    <tr>
        <td style="vertical-align: top">url</td>
        <td style="vertical-align: top; word-wrap: break-word">The URL of the remote endpoint.<br>The url scheme should be either ‘ws’ or ‘wss’.</td>
        <td style="vertical-align: top"></td>
        <td style="vertical-align: top">STRING</td>
        <td style="vertical-align: top">No</td>
        <td style="vertical-align: top">No</td>
    </tr>
    <tr>
        <td style="vertical-align: top">sub.protocol</td>
        <td style="vertical-align: top; word-wrap: break-word">The negotiable sub-protocol if server is asking for it.<br>The sub.protocol should adhere to <code>subprotocol1, subprotocol2,...</code> format.</td>
        <td style="vertical-align: top">null</td>
        <td style="vertical-align: top">STRING</td>
        <td style="vertical-align: top">Yes</td>
        <td style="vertical-align: top">No</td>
    </tr>
    <tr>
        <td style="vertical-align: top">headers</td>
        <td style="vertical-align: top; word-wrap: break-word">Any specific headers which need to send to the server.<br>The headers should adhere to <code>'key1:value1', 'key2:value2',...</code> format.</td>
        <td style="vertical-align: top">null</td>
        <td style="vertical-align: top">STRING</td>
        <td style="vertical-align: top">Yes</td>
        <td style="vertical-align: top">No</td>
    </tr>
    <tr>
        <td style="vertical-align: top">idle.timeout</td>
        <td style="vertical-align: top; word-wrap: break-word">Idle timeout of the connection</td>
        <td style="vertical-align: top">-1</td>
        <td style="vertical-align: top">INT</td>
        <td style="vertical-align: top">Yes</td>
        <td style="vertical-align: top">No</td>
    </tr>
</table>

<span id="examples" class="md-typeset" style="display: block; font-weight: bold;">Examples</span>
<span id="example-1" class="md-typeset" style="display: block; color: rgba(0, 0, 0, 0.54); font-size: 12.8px; font-weight: bold;">EXAMPLE 1</span>
```
@Source(type = ‘websocket’, url = 'ws://localhost:8025/websockets/abc', 
   @map(type='xml'))
define stream Foo (attribute1 string, attribute2 int);
```
<p style="word-wrap: break-word">Under this configuration, events are received via the WebSocket server and they are passed to <code>Foo</code> stream for processing. </p>

### websocket-server *<a target="_blank" href="https://wso2.github.io/siddhi/documentation/siddhi-4.0/#source">(Source)</a>*

<p style="word-wrap: break-word">A Siddhi application can be configured to receive events via the WebSocket by adding the @Source(type = ‘websocket-server’) annotation at the top of an event stream definition.</p>

<span id="syntax" class="md-typeset" style="display: block; font-weight: bold;">Syntax</span>
```
@source(type="websocket-server", host="<STRING>", port="<STRING>", sub.protocol="<STRING>", idle.timeout="<INT>", tls.enabled="<BOOL>", keystore.path="<STRING>", keystore.password="<STRING>", @map(...)))
```

<span id="query-parameters" class="md-typeset" style="display: block; color: rgba(0, 0, 0, 0.54); font-size: 12.8px; font-weight: bold;">QUERY PARAMETERS</span>
<table>
    <tr>
        <th>Name</th>
        <th style="min-width: 20em">Description</th>
        <th>Default Value</th>
        <th>Possible Data Types</th>
        <th>Optional</th>
        <th>Dynamic</th>
    </tr>
    <tr>
        <td style="vertical-align: top">host</td>
        <td style="vertical-align: top; word-wrap: break-word">host of the WebSocket server</td>
        <td style="vertical-align: top"></td>
        <td style="vertical-align: top">STRING</td>
        <td style="vertical-align: top">No</td>
        <td style="vertical-align: top">No</td>
    </tr>
    <tr>
        <td style="vertical-align: top">port</td>
        <td style="vertical-align: top; word-wrap: break-word">port of the WebSocket server</td>
        <td style="vertical-align: top"></td>
        <td style="vertical-align: top">STRING</td>
        <td style="vertical-align: top">No</td>
        <td style="vertical-align: top">No</td>
    </tr>
    <tr>
        <td style="vertical-align: top">sub.protocol</td>
        <td style="vertical-align: top; word-wrap: break-word">Sub-Protocols which are allowed by the service.<br>The sub.protocol should adhere to <code>subprotocol1, subprotocol2,...</code> format.</td>
        <td style="vertical-align: top">null</td>
        <td style="vertical-align: top">STRING</td>
        <td style="vertical-align: top">Yes</td>
        <td style="vertical-align: top">No</td>
    </tr>
    <tr>
        <td style="vertical-align: top">idle.timeout</td>
        <td style="vertical-align: top; word-wrap: break-word">Idle timeout of the connection. If the idle.timeout = '-1' then the timer is disabled.</td>
        <td style="vertical-align: top">-1</td>
        <td style="vertical-align: top">INT</td>
        <td style="vertical-align: top">Yes</td>
        <td style="vertical-align: top">No</td>
    </tr>
    <tr>
        <td style="vertical-align: top">tls.enabled</td>
        <td style="vertical-align: top; word-wrap: break-word">This parameter specifies whether a secure connection is enabled or not. When this parameter is set to <code>true</code>, the <code>keystore.path</code> and the <code>keystore.password</code> parameters are initialized.</td>
        <td style="vertical-align: top">false</td>
        <td style="vertical-align: top">BOOL</td>
        <td style="vertical-align: top">Yes</td>
        <td style="vertical-align: top">No</td>
    </tr>
    <tr>
        <td style="vertical-align: top">keystore.path</td>
        <td style="vertical-align: top; word-wrap: break-word">The file path to the location of the keystore. If a custom keystore is not specified, then the system uses the default keystore file - wso2carbon.jks in the <code>${carbon.home}/resources/security</code> directory.</td>
        <td style="vertical-align: top">${carbon.home}/resources/security/wso2carbon.jks</td>
        <td style="vertical-align: top">STRING</td>
        <td style="vertical-align: top">Yes</td>
        <td style="vertical-align: top">No</td>
    </tr>
    <tr>
        <td style="vertical-align: top">keystore.password</td>
        <td style="vertical-align: top; word-wrap: break-word">The password for the keystore. A custom password can be specified if required. If no custom password is specified, then the system uses <code>wso2carbon</code> as the default password.</td>
        <td style="vertical-align: top">wso2carbon</td>
        <td style="vertical-align: top">STRING</td>
        <td style="vertical-align: top">Yes</td>
        <td style="vertical-align: top">No</td>
    </tr>
</table>

<span id="examples" class="md-typeset" style="display: block; font-weight: bold;">Examples</span>
<span id="example-1" class="md-typeset" style="display: block; color: rgba(0, 0, 0, 0.54); font-size: 12.8px; font-weight: bold;">EXAMPLE 1</span>
```
@Source(type = ‘websocket-server’, host='localhost', port='8025', 
   @map(type='xml'))
define stream Foo (attribute1 string, attribute2 int);
```
<p style="word-wrap: break-word">Under this configuration, events are received via the WebSocket server and they are passed to <code>Foo</code> stream for processing. </p>

