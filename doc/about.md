# About Indigo CDMI StoRM

## Description of CDMI StoRM plugin

In order to integrate StoRM to support QoS through INDIGO CDMI server, 
the [CDMI Service Provider Interface](https://github.com/indigo-dc/cdmi-spi) has been implemented.

### Functions

* **Status of a resource**: CDMI StoRM plugin get the resource info from a StoRM Backend (from v1.11.12) and compute its current QoS profile.
* **Update resource QoS**: CDMI StoRM plugin can update the resource QoS, where allowed. For example can trigger a file recall from tape.

### Architecture

TO-DO