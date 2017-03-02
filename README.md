<a href="https://travis-ci.org/algofi/polymer-minimizer" title="Latest push build on default branch: " target="_polymer-minimizer-build-ci">
            <img src="https://travis-ci.org/algofi/polymer-minimizer.svg?branch=master" alt="build:">
          </a>

This is a maven plugin to build into a single file all dependencies (imports) of a polymer projects.


This project is doing many minification tasks:
* minifying custom web component
* appending all elements to a single bundle.

### Minifying custom web component

For each web component, we will 
* minify its name. A name like `my-custom-element` will be changed to `x-a`.
* minify all its properties name. A property named `sessionId` will be renamed `a` and 
the attribute `session-id` will then be replace by `a`.

Let's say you have this web component :
```html
<dom-module id="x-five-properties">
	<template>
		<style>
		</style>
		{{sessionId}} [[friends]]
		
		[[posts]] [[habilitation.id]]
		
		{{userId.name}}
		<script>
			Polymer( {
				is:'x-five-properties',
				properties: {
					sessionId: Number,
					userId: {
						type: String,
						notify: true,
						readOnly: true,
						observer: '_userIdChanged'
					},
					habilitation: {
						type: Object
					},
					friends: {
						type: Array
					},
					posts: {
						type: Object
					}
				},
				_userIdChanged: function() {
					console.log( this.sessionId, this.userId, this.habilitation.id, this.friends.list, this.posts );
				},
				ready: function () {
					var self = this;
					this.addEventListener( 'tap', function ( e ) {
						console.log( self.sessionId, self.userId, self.habilitation.id, self.friends.list, self.posts );
					} );
				},
				created: function () {
					var me = this;
					this.addEventListener( 'tap', function ( e ) {
						console.log( me.sessionId, me.userId, me.habilitation.id, me.friends.list, me.posts );
					} );
				}
			} );
		</script>
	</template>
</dom-module>
```

Minifying this web component will lead to 
```html
<dom-module id="x-five-properties"><template><style></style>
		{{a}} [[d]]
		
		[[e]] [[c.id]]
		
		{{b.name}}
		<script>
			Polymer( {
				is:'x-five-properties',
				properties: {
					a: Number,
					b: {
						type: String,
						notify: true,
						readOnly: true,
						observer: '_userIdChanged'
					},
					c: {
						type: Object
					},
					d: {
						type: Array
					},
					e: {
						type: Object
					}
				},
				_userIdChanged: function() {
					console.log( this.a, this.b, this.c.id, this.d.list, this.e );
				},
				ready: function () {
					var self = this;
					this.addEventListener( 'tap', function ( e ) {
						console.log( self.a, self.b, self.c.id, self.d.list, self.e );
					} );
				},
				created: function () {
					var me = this;
					this.addEventListener( 'tap', function ( e ) {
						console.log( me.a, me.b, me.c.id, me.d.list, me.e );
					} );
				}
			} );
			
		</script></template></dom-module>
```
