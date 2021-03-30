package org.example;

import graphql.ExecutionResult;
import graphql.GraphQL;
import graphql.schema.DataFetcher;
import graphql.schema.DataFetchingEnvironment;
import graphql.schema.GraphQLSchema;
import graphql.schema.SelectedField;
import graphql.schema.idl.RuntimeWiring;
import graphql.schema.idl.SchemaGenerator;
import graphql.schema.idl.SchemaParser;
import graphql.schema.idl.TypeDefinitionRegistry;

import static graphql.schema.idl.RuntimeWiring.newRuntimeWiring;

public class Reproducer {

    public static void main(String[] args) {
        RuntimeWiring runtimeWiring = newRuntimeWiring()
                .type("Query", builder ->
                        builder.dataFetcher("jim", new DataFetcher() {
                            @Override
                            public Object get(DataFetchingEnvironment environment) {
                                for (SelectedField field : environment.getSelectionSet().getFields()) {
                                    System.out.println("----------------------------");
                                    System.out.println("field name: " + field.getName());
                                    System.out.println("field qualified name: " + field.getQualifiedName());
                                    System.out.println("field fully qualified name: " + field.getFullyQualifiedName());
                                    System.out.println("----------------------------");
                                }
                                return new Person("Jim", "King");
                            }
                        }))
                .build();

        String schema = "type Query{jim: Person}" +
                "type Person {firstName: String, surname: String}";
        SchemaParser schemaParser = new SchemaParser();
        TypeDefinitionRegistry typeDefinitionRegistry = schemaParser.parse(schema);
        SchemaGenerator schemaGenerator = new SchemaGenerator();
        GraphQLSchema graphQLSchema = schemaGenerator.makeExecutableSchema(typeDefinitionRegistry, runtimeWiring);
        GraphQL graphQL = GraphQL.newGraphQL(graphQLSchema).build();

        ExecutionResult executionResult = graphQL.execute("{jim {firstName}}");
        System.out.println(executionResult);
    }

    public static class Person {

        private String firstName;
        private String surname;

        public Person(String firstName, String surname) {
            this.firstName = firstName;
            this.surname = surname;
        }

        public String getFirstName() {
            return firstName;
        }

        public void setFirstName(String firstName) {
            this.firstName = firstName;
        }

        public String getSurname() {
            return surname;
        }

        public void setSurname(String surname) {
            this.surname = surname;
        }
    }


}
