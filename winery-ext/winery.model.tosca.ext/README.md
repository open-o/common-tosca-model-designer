���ڱ���winery.model.tosca.ext��Ҫ�������������������������Ҫ��pom�����ô����ַ��Ϊ��֤��Դ�����ͨ���ԣ�git���ϵĴ��벻���ô�����Ϣ��
��ģ���������ɵ�jar�ѷ������ִ��ϣ������Ҫ���£��밴���·����������ã�Ȼ���ֶ����벢�����ִ���

���ô�����Ϊ���� jaxb2-maven-plugin �µ� configuration �����Ԫ�� httpproxy �����磺

<plugin>
    <groupId>org.codehaus.mojo</groupId>
    <artifactId>jaxb2-maven-plugin</artifactId>
    <version>1.5</version>
    <executions>
        <execution>
            <phase>generate-sources</phase>
            <goals>
                <goal>xjc</goal>
            </goals>
            <configuration>
                <schemaDirectory>${project.basedir}/src/main/resources</schemaDirectory>
                <!--<schemaFiles>TOSCA-v1.0-cos01.xsd</schemaFiles>-->
                <httpproxy>proxynj.zte.com.cn:80</httpproxy>
                <packageName>org.eclipse.winery.model.tosca</packageName>
            </configuration>
        </execution>
    </executions>
</plugin>
