<xs:schema xmlns:xs="http://www.w3.org/2001/XMLSchema">
	<xs:group name="itemgroup">
		<xs:choice>
			<xs:element name="singleitem">
				<xs:complexType>
					<xs:attribute name="desc" use="optional">
						<xs:simpleType>
							<xs:restriction base="xs:string">
								<xs:minLength value="1" />
							</xs:restriction>
						</xs:simpleType>
					</xs:attribute>	
					
					<xs:attribute name="name" use="required">
						<xs:simpleType>
							<xs:restriction base="xs:string">
								<xs:minLength value="1" />
							</xs:restriction>
						</xs:simpleType>
					</xs:attribute>

					<xs:attribute name="type" use="required">
						<xs:simpleType>
							<xs:restriction base="xs:string">
								<xs:enumeration value="byte" />
								<xs:enumeration value="unsigned byte" />
								<xs:enumeration value="short" />
								<xs:enumeration value="unsigned short" />
								<xs:enumeration value="integer" />
								<xs:enumeration value="unsigned integer" />
								<xs:enumeration value="long" />
								<xs:enumeration value="ub pascal string" />
								<xs:enumeration value="us pascal string" />
								<xs:enumeration value="si pascal string" />
								<xs:enumeration value="fixed length string" />
								<xs:enumeration value="ub variable length byte[]" />
								<xs:enumeration value="us variable length byte[]" />
								<xs:enumeration value="si variable length byte[]" />
								<xs:enumeration value="fixed length byte[]" />
								<xs:enumeration value="java sql date" />
								<xs:enumeration value="java sql timestamp" />
								<xs:enumeration value="boolean" />
							</xs:restriction>
						</xs:simpleType>
					</xs:attribute>

					<xs:attribute name="size" use="optional">
						<xs:simpleType>
							<xs:restriction base="xs:string">
								<xs:minLength value="1" />
							</xs:restriction>
						</xs:simpleType>
					</xs:attribute>

					<xs:attribute name="charset" use="optional">
						<xs:simpleType>
							<xs:restriction base="xs:string">
								<xs:minLength value="1" />
							</xs:restriction>
						</xs:simpleType>
					</xs:attribute>

					<xs:attribute name="defaultValue" use="optional">
						<xs:simpleType>
							<xs:restriction base="xs:string">
								<xs:minLength value="1" />
							</xs:restriction>
						</xs:simpleType>
					</xs:attribute>
				</xs:complexType>
			</xs:element>

			<xs:element name="array">
				<xs:complexType>
					<xs:sequence>
						<xs:group minOccurs="0" maxOccurs="unbounded" ref="itemgroup" />
					</xs:sequence>

					<xs:attribute name="name" use="required">
						<xs:simpleType>
							<xs:restriction base="xs:string">
								<xs:minLength value="1" />
							</xs:restriction>
						</xs:simpleType>
					</xs:attribute>
					<xs:attribute name="cnttype" use="required">
						<xs:simpleType>
							<xs:restriction base="xs:string">
								<xs:enumeration value="reference" />
								<xs:enumeration value="direct" />
							</xs:restriction>
						</xs:simpleType>
					</xs:attribute>
					<xs:attribute name="cntvalue" use="required">
						<xs:simpleType>
							<xs:restriction base="xs:string">
								<xs:minLength value="1" />
							</xs:restriction>
						</xs:simpleType>
					</xs:attribute>
				</xs:complexType>
			</xs:element>
		</xs:choice>
	</xs:group>

	<xs:element name="sinnori_message">
		<xs:complexType>
			<xs:sequence>
				<xs:element name="messageID" minOccurs="1" maxOccurs="1">
					<xs:simpleType>
						<xs:restriction base="xs:string">
							<xs:pattern value="[a-zA-Z][a-zA-Z1-9]+" />
						</xs:restriction>
					</xs:simpleType>
				</xs:element>
				<xs:element name="direction" minOccurs="1" maxOccurs="1">
					<xs:simpleType>
						<xs:restriction base="xs:string">
							<xs:pattern value="[a-zA-Z][a-zA-Z1-9_]+" />
						</xs:restriction>
					</xs:simpleType>
				</xs:element>
				<xs:element name="desc" type="xs:string" minOccurs="0" maxOccurs="1" />
				<xs:group minOccurs="0" maxOccurs="unbounded" ref="itemgroup" />
			</xs:sequence>
		</xs:complexType>
	</xs:element>
</xs:schema>