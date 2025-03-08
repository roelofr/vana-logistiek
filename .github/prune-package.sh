#!/usr/bin/env bash

package_owner="$1"
package_name="$2"

# Check usage
if [ -z "$package_owner" ] || [ -z "$package_name" ]; then
    echo "Usage: ${BASH_SOURCE[0]} <owner> <name>"
    exit 1
fi

# Find packages
readarray -t orphan_version_ids <<<"$(
    gh api \
        --paginate \
        --jq '.[] | select((.metadata.container.tags | length) <= 1) | .id' \
        /users/${package_owner}/packages/container/${package_name}/versions \
    | sed 's/\n/ /'
)"

orphan_count="${#orphan_version_ids[@]}"

echo "Found ${orphan_count} items to remove."

if [ "$orphan_count" == "0" ]; then
    echo "All good :)"
    exit 0
fi

set -e

for version_id in "${orphan_version_ids[@]}";
do
    echo "Removing version ${version_id}"
    gh api \
        --silent \
        --method DELETE \
        --header 'X-GitHub-Api-Version:2022-11-28' \
        "/users/${package_owner}/packages/container/${package_name}/versions/${version_id}"
done
