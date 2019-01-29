AUI.add(
	'liferay-ddm-form-field-file-upload',
	function(A) {
		var Lang = A.Lang;

		var FileUploadField = A.Component.create(
			{
				ATTRS: {
					portletNamespace: {
						value: ''
					},

					type: {
						value: 'file_upload'
					},

					uploader: {
						value: 'file_upload'
					},

					value: {
						value: ''
					}
				},

				EXTENDS: Liferay.DDM.Renderer.Field,

				NAME: 'liferay-ddm-form-field-file-upload',

				prototype: {

					getTemplateContext: function() {
						var instance = this;

						return A.merge(
							FileUploadField.superclass.getTemplateContext.apply(instance, arguments),
							{
								fileEntryTitle: instance._getFileEntryTitle(),
								value: JSON.stringify(instance.getValue())
							}
						);
					},

					getValue: function() {
						var instance = this;

						return instance.get('value');
					},

					render: function(target) {
						var instance = this;

						var container = instance.get('container');

						var parent = instance.get('parent');

						if (target && !parent) {
							container.appendTo(target);
						}

						container.setContent(instance.getTemplate());

						instance.eachNestedField(
							function(field) {
								field.updateContainer();
							}
						);

						var portletNamespace = instance.get('portletNamespace');

						var uploadURL = instance.get('uploadURL');

						if (uploadURL && !instance.get('readOnly')) {
							instance.uploader = new Liferay.Upload(
								{
									boundingBox: '#' + portletNamespace + 'fileUpload',
									multipleFiles: false,
									namespace: portletNamespace,
									simultaneousUploads: 1,
									uploadFile: uploadURL
								}
							);

							instance.uploader.on(
								'uploadComplete',
								function(event) {
									instance.setValue(
										{
											name: event.name,
										}
									);

									instance._fireStartedFillingEvent();
								}
							);
						}

						instance.fire('render');

						instance.set('rendered', true);

						return instance;
					},

					setValue: function(value) {
						var instance = this;

						instance.set('value', value);

						instance.render();
					},

					showErrorMessage: function() {
						var instance = this;

						var container = instance.get('container');

						FileUploadField.superclass.showErrorMessage.apply(instance, arguments);

						container.all('.form-feedback-indicator').appendTo(container.one('.form-group'));
					},

					_getFileEntryTitle: function() {
						var instance = this;

						var value = instance.getValue();

						if (value) {
							return JSON.parse(value).name;
						}

						var fileEntryTitle = instance.get('fileEntryTitle');

						if (fileEntryTitle) {
							return fileEntryTitle;
						}

						return '';
					},
				}
			}
		);

		Liferay.namespace('DDM.Field').FileUpload = FileUploadField;
	},
	'', {
		requires: ['liferay-ddm-form-renderer-field', 'liferay-upload']
	}
);