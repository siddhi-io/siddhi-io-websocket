# API Docs - v1.0.0-SNAPSHOT

## Sink

### websocket *<a target="_blank" href="https://wso2.github.io/siddhi/documentation/siddhi-4.0/#sink">(Sink)</a>*

<p style="word-wrap: break-word">A Siddhi application can be configured to publish events via the Websocket transport by adding the @Sink(type = ‘Websocket’) annotation at the top of an event stream definition.</p>

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
@Sink(type = ‘websocket’, url = 'ws://localhost:8025/websockets/abc', 
   @map(type='xml'))
define stream Foo (attribute1 string, attribute2 int);
```
<p style="word-wrap: break-word">A sink of type 'websocket' has been defined.<br>All events arriving at Foo stream via websocket will be sent to the url ws://localhost:8025/websockets/abc.</p>

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

