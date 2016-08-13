由于编译winery.model.tosca.ext需要连接外网，如果是内网环境需要在pom中设置代理地址，为保证开源代码的通用性，git库上的代码不设置代理信息。
该模块编译后生成的jar已发布到仓储上，如果需要更新，请按如下方法进行设置，然后手动编译并发布仓储。

设置代理方法为：在 jaxb2-maven-plugin 下的 configuration 添加子元素 httpproxy ，例如：

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
