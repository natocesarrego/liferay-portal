#if (${liferayVersion.startsWith("7.2")})
import '../FieldBase/FieldBase.es';
import './${artifactId}Register.soy.js';
import templates from './${artifactId}.soy.js';
import {Config} from 'metal-state';
#else
import templates from './${artifactId}.soy';
#end
import Component from 'metal-component';
import Soy from 'metal-soy';


/**
 * ${className} Component
 */
class ${className} extends Component {}

#if (${liferayVersion.startsWith("7.2")})
${className}.STATE = {

	/**
	 * @default undefined
	 * @instance
	 * @memberof Text
	 * @type {?(string|undefined)}
	 */

	name: Config.string().required(),

	/**
	 * @default undefined
	 * @instance
	 * @memberof Text
	 * @type {?(string|undefined)}
	 */

	value: Config.string().value('')
}

Soy.register(${className}, templates);
#else
// Register component
Soy.register(${className}, templates, 'render');

if (!window.DDM${className}) {
	window.DDM${className} = {

	};
}

window.DDM${className}.render = ${className};
#end
export default ${className};