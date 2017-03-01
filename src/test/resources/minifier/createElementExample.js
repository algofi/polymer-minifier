<dom-module id="x-five-properties">
	<template>
		<script>
			Polymer( {
				is:'x-five-properties',
				ready: function () {
					var element = document.createElement( 'paper-checkbox');
					element.checked = true;
				}
			} );
		</script>
	</template>
</dom-module>
