module.exports = {
  extends: ['@commitlint/config-conventional'],
  rules: {
    // Disabled: Dependabot's "Bump X from Y to Z" subject is always
    // capitalized and isn't configurable, so this rule fails every
    // automated dependency-bump PR otherwise.
    'subject-case': [0],
  },
};
